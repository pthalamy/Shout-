package es.upm.dam2016g6.shout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_map) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                } else if (tabId == R.id.tab_chatrooms) {

                } else if (tabId == R.id.tab_pm) {

                } else {
                    
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_map) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                } else if (tabId == R.id.tab_chatrooms) {

                } else if (tabId == R.id.tab_pm) {

                } else {

                }
            }
        });

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String profileImgUrl = user.getPhotoUrl().toString();

        Glide.with(MyProfileActivity.this)
                .load(profileImgUrl)
                .into((ImageView)findViewById(R.id.img_profilePic));

        ((TextView)findViewById(R.id.tv_welcome)).setText(user.getDisplayName());

        // Retrieve Facebook User Likes
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        /* make the API call */
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Handle result
                        try
                        {
//                            System.out.println(response);
                            JSONArray JSONFbLikes = object.getJSONObject("likes").getJSONArray("data");
//                            System.out.println(JSONFbLikes);

                            List<FacebookLike> fbLikes = new ArrayList<FacebookLike>(JSONFbLikes.length());

                            for (int i = 0; i < JSONFbLikes.length(); i++) {
                                JSONObject fbLike = JSONFbLikes.getJSONObject(i);
//                                System.out.println(fbLike.getString("name"));
                                fbLikes.add(new FacebookLike(fbLike.getString("name"), fbLike.getString("id")));
                            }

                            RecyclerView rv = (RecyclerView) findViewById(R.id.rv_mylikes);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(MyProfileActivity.this);
                            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            layoutManager.scrollToPosition(0);
                            rv.setLayoutManager(layoutManager);
                            MyLikesRecyclerViewAdapter adapter = new MyLikesRecyclerViewAdapter(fbLikes);
                            rv.setAdapter(adapter);
                        }
                        catch ( Throwable t )
                        {
                            System.err.println( t );
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "likes");
        request.setParameters(parameters);
        request.executeAsync();

    }

    // Toolbar and Menu Methods

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_profile_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
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

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}

