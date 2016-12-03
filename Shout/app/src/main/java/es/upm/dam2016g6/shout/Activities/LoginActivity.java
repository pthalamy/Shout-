package es.upm.dam2016g6.shout.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.upm.dam2016g6.shout.Fragments.MyProfileFragment;
import es.upm.dam2016g6.shout.Model.User;
import es.upm.dam2016g6.shout.R;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TAG_LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            System.out.println("User already signed in.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // not signed in
            System.out.println("User not signed in.");
            presentFirebaseSignIn();
        }
    }

    // Login response handler
    // Test users can be found on the Facebook developer console
    // email1: pzrzpdbpsn_1479747064@tfbnw.net
    // Passwords: dam12345678
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // user is signed in!
            System.out.println("User is now signed in!");

//            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
//            String fbToken = idpResponse.getIdpToken();
//            System.out.println("fbToken: " + fbToken);

            // User is signed-in add or update the database to add him
            // Create new User
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locations");
            FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
            GeoFire geoFire = new GeoFire(ref);

            User me = User.addNewUser(authUser.getUid(), authUser.getDisplayName());
            geoFire.setLocation(me.getUserId(), new GeoLocation(0.0, 0.0), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        Log.d(LoginActivity.TAG, "There was an error saving the location to GeoFire: " + error);
                    } else {
                        Log.d(LoginActivity.TAG, "Location saved on server successfully!");
                    }
                }
            });
            startActivity(new Intent(this, MainActivity.class));

            finish();
            return;
        }

        // Sign in canceled
        if (resultCode == RESULT_CANCELED) {
//            showSnackbar(R.string.sign_in_cancelled);
            View parentLayout = findViewById(R.id.activity_login);
            Snackbar.make(parentLayout, R.string.sign_in_cancelled, Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }

        // No network
        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            View parentLayout = findViewById(R.id.activity_login);
            Snackbar.make(parentLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }

        // User is not signed in. Maybe just wait for the user to press
        // "sign in" again, or show a message.
        presentFirebaseSignIn();
    }

    // Present Firebase AuthUI logging view, with Facebook as only possible provider
    private void presentFirebaseSignIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Configure Facebook permissions
        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                .setPermissions(getFacebookPermission())
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(Arrays.asList(facebookIdp))
                        .setLogo(R.drawable.ic_shoutlogo)
                        .setIsSmartLockEnabled(true)
                        .build(),
                RC_SIGN_IN);

    }

    // Setup permissions needed for the app to access the user's facebook data
    private List<String> getFacebookPermission() {
        List<String> permissions = new ArrayList<>();

        permissions.add("user_likes");
        permissions.add("user_friends");

        return permissions;
    }

// Retrieve token ID if needed
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
//            startActivity(new Intent(this, WelcomeBackActivity.class)
//                    .putExtra("my_token", idpResponse.getIdpToken()));
//        }
//    }
}
