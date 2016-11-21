package es.upm.dam2016g6.shout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static es.upm.dam2016g6.shout.R.id.img_profilePic;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String profileImgUrl = user.getPhotoUrl().toString();

        Glide.with(MyProfileActivity.this)
                .load(profileImgUrl)
                .into((ImageView)findViewById(img_profilePic));

        ((TextView)findViewById(R.id.tv_welcome)).setText("Welcome " + user.getDisplayName());
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            System.err.println("User signed out");
                            startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
        }
    }
}

