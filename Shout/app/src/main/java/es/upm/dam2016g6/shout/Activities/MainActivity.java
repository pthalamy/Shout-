package es.upm.dam2016g6.shout.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.fragments.ChatRoomsFragment;
import es.upm.dam2016g6.shout.fragments.DiscoveryFragment;
import es.upm.dam2016g6.shout.fragments.MyProfileFragment;
import es.upm.dam2016g6.shout.fragments.PrivateConversationsFragment;
import es.upm.dam2016g6.shout.model.ChatRoom;
import es.upm.dam2016g6.shout.model.User;
import es.upm.dam2016g6.shout.support.Utils;

public class MainActivity extends AppCompatActivity
        implements MyProfileFragment.OnProfileInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static final String TAG = "TAG_MainActivity";
    private static final int REQUEST_CHECK_SETTINGS = 0x01;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "RLUK_MainActivity";
    private static final String LOCATION_KEY = "LK_MainActivity";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "LUTSK_MainActivity";
    public static final String DISCOVERY_FRAGMENT = "DISCOVERY_FRAGMENT";
    public static final String CHATROOMS_FRAGMENT_NEARBY = "CHATROOMS_FRAGMENT_NEARBY";
    public static final String FRAGMENT_TO_INFLATE = "FRAGMENT_TO_INFLATE";
    public static final String CHATROOMS_FRAGMENT_MYCHATROOMS = "CHATROOMS_FRAGMENT_MYCHATROOMS";
    public static final String PRIVATECONVERSATIONS_FRAGMENT = "PRIVATECONVERSATIONS_FRAGMENT";

    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public Location mCurrentLocation;
    public GeoLocation mCurrentGeoLocation;
    private String mLastUpdateTime;

    private User mActiveUser;
    private DatabaseReference mActiveUserRef;
    private ValueEventListener mActiveUserVEL;

    private Fragment fragment;
    public HashMap<String, User> usersInRange = new HashMap<>();
    public HashMap<String, ChatRoom> chatroomsInRange = new HashMap<>();

    // Store to speed up location updates //
    public GeoFire geoFireUsers;
    public GeoFire geoFireChatrooms;
    private String userId;
    private GeoQuery mGeoQueryUsers;
    private GeoQuery mGeoQueryChatrooms;
    public final double discoveryRadius = 1; // In kilometers
    // end //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Fetch current User
        mActiveUserRef = Utils.getDatabase().getReference("/users/" + Utils.getCurrentUserUid());
        mActiveUserVEL = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mActiveUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCreate: active user's data could not be fetched from Firebase");
            }
        };
        mActiveUserRef.addValueEventListener(mActiveUserVEL);

        geoFireUsers = new GeoFire(Utils.getDatabase().getReference("userLocations"));
        geoFireChatrooms = new GeoFire(Utils.getDatabase().getReference("chatroomLocations"));
        userId = Utils.getCurrentUserUid();

        // Set up bottom bar if not previously done
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                MainActivity.this.fragment = null;

                if (tabId == R.id.tab_map) {
                    fragment = new DiscoveryFragment();
                } else if (tabId == R.id.tab_chatrooms) {
                    fragment = new ChatRoomsFragment();
                } else if (tabId == R.id.tab_pm) {
                    fragment = new PrivateConversationsFragment();
                } else { // tabProfile
                    fragment = new MyProfileFragment();
                }

                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
            }
        });

        Intent intent = getIntent();
        String param = intent.getStringExtra(MainActivity.FRAGMENT_TO_INFLATE);
        if (param != null) {
            switch (param) {
                case CHATROOMS_FRAGMENT_NEARBY:
                case CHATROOMS_FRAGMENT_MYCHATROOMS:
                    bottomBar.selectTabAtPosition(1);
                    break;
                case PRIVATECONVERSATIONS_FRAGMENT:
                    bottomBar.selectTabAtPosition(2);
                    break;
                default:
                    bottomBar.selectTabAtPosition(0);
            }
        }

        mCurrentGeoLocation = Utils.getCurrentLocation(this);
        // creates a new query around user's location, with a radius of discoverRadius kilometers
        mGeoQueryUsers = geoFireUsers.queryAtLocation(mCurrentGeoLocation, discoveryRadius);
        mGeoQueryUsers.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(MainActivity.this.TAG, String.format("User Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                Query userInRangeQuery = Utils.getDatabase().getReference().child("users").child(key);
                userInRangeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        usersInRange.put(user.uid, user);
                        if (fragment instanceof DiscoveryFragment) {
                            DiscoveryFragment df = (DiscoveryFragment)fragment;
                            df.drawUser(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getChatroomInRange:onCancelled", databaseError.toException());
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                Log.d(MainActivity.this.TAG, String.format("User Key %s is no longer in the search area", key));
                usersInRange.remove(key);
                if (fragment instanceof DiscoveryFragment) {
                    DiscoveryFragment df = (DiscoveryFragment)fragment;
                    df.undrawUser(key);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(MainActivity.this.TAG, String.format("User Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                if (fragment instanceof DiscoveryFragment) {
                    DiscoveryFragment df = (DiscoveryFragment)fragment;
                    df.updateUserLocation(key, location);
                }
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(MainActivity.this.TAG, "All initial user data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(MainActivity.this.TAG, "There was an error with this user query: " + error);
            }
        });

        // creates a new query around user's location, with a radius of discoverRadius kilometers
        mGeoQueryChatrooms = geoFireUsers.queryAtLocation(mCurrentGeoLocation, discoveryRadius);
        mGeoQueryChatrooms.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(MainActivity.this.TAG, String.format("Chatroom Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                Query chatroomInRangeQuery = Utils.getDatabase().getReference().child("chatrooms").child(key);
                chatroomInRangeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                        chatroomsInRange.put(chatRoom.uid, chatRoom);
                        if (fragment instanceof DiscoveryFragment) {
                            DiscoveryFragment df = (DiscoveryFragment)fragment;
                            df.drawChatroom(chatRoom);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getChatroomInRange:onCancelled", databaseError.toException());
                    }
                });

            }

            @Override
            public void onKeyExited(String key) {
                Log.d(MainActivity.this.TAG, String.format("Chatroom Key %s is no longer in the search area", key));
                // chatroom has expired
                chatroomsInRange.remove(key);
                if (fragment instanceof DiscoveryFragment) {
                    DiscoveryFragment df = (DiscoveryFragment)fragment;
                    df.undrawChatroom(key);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(MainActivity.this.TAG, String.format("Chatroom Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                // Chatroom should not be able to move
                // TODO: 20/12/16 update object location
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(MainActivity.this.TAG, "All initial chatroom  data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(MainActivity.this.TAG, "There was an error with this chatroom query: " + error);
            }
        });

        // Set up location requests for location updates
        createLocationRequest();
    }

    // Toolbar and Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (fragment instanceof MyProfileFragment)
            this.getMenuInflater().inflate(R.menu.my_profile_menu, menu);
        else if (fragment instanceof ChatRoomsFragment)
            this.getMenuInflater().inflate(R.menu.chat_rooms_menu, menu);
        else if (fragment instanceof PrivateConversationsFragment)
            this.getMenuInflater().inflate(R.menu.pm_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
                deleteLocationFromFirebase(authUser.getUid());
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                Log.d(TAG, "User signed out");
                                Utils.resetCurrentUserUid();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                mActiveUserRef.removeEventListener(mActiveUserVEL); // Safely disable listener before destroying activity
                                finish();
                            }
                        });
                return true;

            case R.id.action_new_chat_rooms:
                startActivity(new Intent(this, ChatRoomCreationActivity.class));
                return true;

            case R.id.action_new_friend_conv:
                Intent intentNFC = new Intent(this, ShowUserListActivity.class);
                String keyRefNFC = "/users/" + Utils.getCurrentUserUid() + "/friends/";
                intentNFC.putExtra(ShowUserListActivity.REF_PATH, keyRefNFC);
                intentNFC.putExtra(ShowUserListActivity.TARGET, ShowUserListActivity.TARGET_CREATECONVO);
                if (mActiveUser != null)
                    intentNFC.putExtra(ShowUserListActivity.NUM_USERS, mActiveUser.friends.size());

                this.startActivity(intentNFC);

                return true;

            case R.id.action_showFriendList:
                Intent intentSF = new Intent(this, ShowUserListActivity.class);
                String keyRefSF = "/users/" + Utils.getCurrentUserUid() + "/friends/";
                intentSF.putExtra(ShowUserListActivity.REF_PATH, keyRefSF);
                intentSF.putExtra(ShowUserListActivity.TARGET, ShowUserListActivity.TARGET_FRIENDLIST);
                if (mActiveUser != null)
                    intentSF.putExtra(ShowUserListActivity.NUM_USERS, mActiveUser.friends.size());

                this.startActivity(intentSF);

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onProfileInteraction() {
        // If something needs to be processed for profile fragment
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.d(MainActivity.TAG, "Initializing location requests...");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        // DO SOMETHING
                        Log.d(MainActivity.TAG, "Unable to change location settings...");
                        break;
                }

            }
        });

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: 28/11/16 Is there anything to do?
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Permissions missing for location updates!");
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Utils.MY_PERMISSIONS_ACCESS_LOCATION);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_ACCESS_LOCATION:
                startLocationUpdates();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO: 28/11/16 Do more than that
        // Location has changed, update GeoFire and local location
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mCurrentGeoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        Utils.setCurrentLocation(mCurrentGeoLocation);

        // Update general location
        geoFireUsers.setLocation(userId, mCurrentGeoLocation);
        Log.d(TAG, mLastUpdateTime + ": Location changed -> (" + location.getLatitude()
                + ", " + location.getLongitude() + ")");

        // Update location in user as well
        DatabaseReference ref = Utils.getDatabase().getReference();
        String userUid = Utils.getCurrentUserUid();
        ref.child("/users/" + userUid + "/location/")
                .setValue(new LatLng(mCurrentGeoLocation.latitude, mCurrentGeoLocation.longitude));

        // Update GeoQueries
        mGeoQueryUsers.setCenter(mCurrentGeoLocation);
        mGeoQueryChatrooms.setCenter(mCurrentGeoLocation);
    }

    private static void deleteLocationFromFirebase(String uid) {
        DatabaseReference ref = Utils.getDatabase().getReference("locations");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(uid);
    }

}