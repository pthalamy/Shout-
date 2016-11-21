package es.upm.dam2016g6.shout;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class LoginActivity extends AppCompatActivity {

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
            startActivity(new Intent(this, MyProfileActivity.class));
        } else {
            // not signed in
            System.out.println("User not signed in.");
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
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
            startActivity(new Intent(this, MyProfileActivity.class));

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
            return;
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
            return;
        }

        // User is not signed in. Maybe just wait for the user to press
        // "sign in" again, or show a message.
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
