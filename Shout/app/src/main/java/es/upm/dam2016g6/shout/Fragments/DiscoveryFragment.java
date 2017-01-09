package es.upm.dam2016g6.shout.fragments;


import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.activities.MainActivity;
import es.upm.dam2016g6.shout.model.ChatRoom;
import es.upm.dam2016g6.shout.model.ShoutLocation;
import es.upm.dam2016g6.shout.model.User;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.internal.FacebookDialogFragment.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoveryFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<User>, ClusterManager.OnClusterInfoWindowClickListener<User>, ClusterManager.OnClusterItemClickListener<User>, ClusterManager.OnClusterItemInfoWindowClickListener<User> {


    MapView mMapView;
    private GoogleMap googleMap;
    private HashMap<String, Marker> mUserMarkers = new HashMap<>(); // Markers for users, indexed by key
    private HashMap<String, Marker> mChatroomMarkers = new HashMap<>(); // Markers for chatrooms, indexed by keya
    private MainActivity mainActivity;
    private HashMap<String, User> mUsers = new HashMap<>();
    private HashMap<String, ChatRoom> mChatrooms = new HashMap<>();

    private ClusterManager<User> mClusterManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_discovery, container, false);

        // Add toolbar to fragment (contains logout button)
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_discovery);
        toolbar.setTitle("Discovery");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mainActivity = (MainActivity)DiscoveryFragment.this.getActivity();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(DiscoveryFragment.this.getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(DiscoveryFragment.this.getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    DiscoveryFragment.this.getActivity()
                            .requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, getTargetRequestCode());
                }

                googleMap.setMyLocationEnabled(true);


                // Draw already loaded users and chatrooms
                for (User user : DiscoveryFragment.this.mainActivity.usersInRange.values()) {
                    drawUser(user);
                }

                for (ChatRoom chatroom : DiscoveryFragment.this.mainActivity.chatroomsInRange.values()) {
                    drawChatroom(chatroom);
                }

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mainActivity.mCurrentGeoLocation.latitude, mainActivity.mCurrentGeoLocation.longitude))
                        .zoom((float) (mainActivity.discoveryRadius * 9))
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                //this part adds the markers to the map.
                mClusterManager = new ClusterManager<User>(DiscoveryFragment.super.getContext(), getMap());
                mClusterManager.setRenderer(new PersonRenderer());
                getMap().setOnCameraIdleListener(mClusterManager);
                getMap().setOnMarkerClickListener(mClusterManager);
                getMap().setOnInfoWindowClickListener(mClusterManager);
                mClusterManager.setOnClusterClickListener(DiscoveryFragment.this);
                mClusterManager.setOnClusterInfoWindowClickListener(DiscoveryFragment.this);
                mClusterManager.setOnClusterItemClickListener(DiscoveryFragment.this);
                mClusterManager.setOnClusterItemInfoWindowClickListener(DiscoveryFragment.this);

                //addItems();
                mClusterManager.cluster();

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    public GoogleMap getMap(){
        return googleMap;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    public void drawUser(User user) {
        mUsers.put(user.uid, user);
        mainActivity.geoFireUsers.getLocation(user.uid, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    User user = mUsers.get(key);
                    user.location = new ShoutLocation(location.latitude,location.longitude);

                    Marker userMarker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.latitude, location.longitude))
                            .title(user.name)
                            .snippet(user.uid)
                            .icon(BitmapDescriptorFactory.fromBitmap(getUserPic(user.facebookId))));


                    //mUserMarkers.put(user.uid, userMarker);
                    //mClusterManager.addItem(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void drawChatroom(final ChatRoom chatroom) {
        mChatrooms.put(chatroom.uid, chatroom);
        mainActivity.geoFireUsers.getLocation(chatroom.uid, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    ChatRoom chatRoom = mChatrooms.get(key);
                    Marker chatroomMarker= googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.latitude, location.longitude))
                            .title(chatroom.title)
                            .snippet(chatroom.uid));
                    chatroomMarker.setAlpha((float) 0.5);
                    mUserMarkers.put(chatroom.uid, chatroomMarker);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void undrawUser(String userKey) {
        Marker marker = mUserMarkers.get(userKey);
        if (marker != null) {
            mUserMarkers.remove(userKey);
            mUsers.remove(userKey);
            marker.remove();
        }
    }

    public void undrawChatroom(String chatroomKey) {
        Marker marker = mChatroomMarkers.get(chatroomKey);
        if (marker != null) {
            mChatroomMarkers.remove(chatroomKey);
            mChatrooms.remove(chatroomKey);
            marker.remove();
        }
    }

    public void updateUserLocation(String userKey, GeoLocation location) {
        Marker marker = mUserMarkers.get(userKey);
        if (marker != null)
            marker.setPosition(new LatLng(location.latitude, location.longitude));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public boolean onClusterClick(Cluster<User> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<User> cluster) {

    }

    @Override
    public boolean onClusterItemClick(User item) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(User item) {

    }


    private class PersonRenderer extends DefaultClusterRenderer<User> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), getMap(), mClusterManager);


            View multiProfile = LayoutInflater.from(getContext()).inflate(R.layout.fragment_discovery,null);
            //getLayoutInflater().inflate(R.layout.fragment_discovery, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }
    }



    public Bitmap getUserPic(String userID) {
        String imageURL;
        Bitmap bitmap = null;
        Log.d(TAG, "Loading Picture");
        imageURL = "https://graph.facebook.com/"+userID+"/picture?type=small";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageURL).getContent());
        } catch (Exception e) {
            Log.d("TAG", "Loading Picture FAILED");
            e.printStackTrace();
        }
        return bitmap;
    }
}
