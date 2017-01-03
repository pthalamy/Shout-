package es.upm.dam2016g6.shout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.FacebookLike;
import es.upm.dam2016g6.shout.model.User;
import es.upm.dam2016g6.shout.support.MyLikesRecyclerViewAdapter;
import es.upm.dam2016g6.shout.support.Utils;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "TAG_" + ProfileActivity.class.getSimpleName();

    public static final String USER_UID = "PROFILE_INTENT_UID";

    private List<FacebookLike> fbCommonLikes;
    private Set<FacebookLike> currentUserLikes;
    private Set<FacebookLike> otherUserLikes;
    private DatabaseReference mUserRef;
    private String userUid;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Parse intent
        Intent intent = getIntent();
        userUid = intent.getStringExtra(USER_UID);
        mUserRef= Utils.getDatabase().getReference("/users/" + userUid);
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                loadUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "ProfileActivity: onCreate: Could not retrieve user information");
            }
        });

        // Add toolbar to fragment (contains logout button)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitle("Profile");
        this.setSupportActionBar(toolbar);

        mUserRef = Utils.getDatabase().getReference("");
    }

    public void loadUserData() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String profileImgUrl = Utils.getFacebookProfilePictureFromID(user.facebookId);

        // Load profile picture from Firebase
        Glide.with(this)
                .load(profileImgUrl)
                .into((CircleImageView) findViewById(R.id.profile_civ_profilePic));

        ((TextView) findViewById(R.id.profile_tv_name)).setText(user.name);
        // Store likes into a recycler view and configure it
        RecyclerView rv = (RecyclerView) this.findViewById(R.id.profile_rv_commonLikes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);

        // Fetch likes for both current user and profile being visited, and
        //  whichever request finishes first initiates likes comparison
        GraphRequest.Callback userCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                // Handle result
                try {
                    JSONObject object = response.getJSONObject();
                    JSONArray JSONFbLikes = object.getJSONObject("likes").getJSONArray("data");
                    currentUserLikes=  new HashSet<FacebookLike>(JSONFbLikes.length());

                    for (int i = 0; i < JSONFbLikes.length(); i++) {
                        JSONObject fbLike = JSONFbLikes.getJSONObject(i);
                        // Instantiate new like and fetch page image asynchronously
                        currentUserLikes.add(new FacebookLike(fbLike.getString("name"), fbLike.getString("id")));
                    }

                    if (otherUserLikes != null)
                        initiateLikesComparison();
                } catch (Throwable t) {
                    System.err.println(t);
                }
            }
        } ;
        Utils.getFacebookLikesForID(AccessToken.getCurrentAccessToken().getUserId(), userCallback);

        GraphRequest.Callback otherCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                // Handle result
                try {
                    JSONObject object = response.getJSONObject();
                    JSONArray JSONFbLikes = object.getJSONObject("likes").getJSONArray("data");
                    otherUserLikes=  new HashSet<FacebookLike>(JSONFbLikes.length());

                    for (int i = 0; i < JSONFbLikes.length(); i++) {
                        JSONObject fbLike = JSONFbLikes.getJSONObject(i);
                        // Instantiate new like and fetch page image asynchronously
                        otherUserLikes.add(new FacebookLike(fbLike.getString("name"), fbLike.getString("id")));
                    }

                    if (currentUserLikes != null)
                        initiateLikesComparison();
                } catch (Throwable t) {
                    System.err.println(t);
                }
            }
        };
        Utils.getFacebookLikesForID(user.facebookId, otherCallback);
    }

    private void initiateLikesComparison() {
        fbCommonLikes =  new ArrayList<FacebookLike>();
        for (FacebookLike like : currentUserLikes) {
            if (otherUserLikes.contains(like))
                fbCommonLikes.add(like);
        }

        MyLikesRecyclerViewAdapter adapter = new MyLikesRecyclerViewAdapter(fbCommonLikes);
        RecyclerView rv = (RecyclerView) ProfileActivity.this.findViewById(R.id.profile_rv_commonLikes);
        rv.setAdapter(adapter);
    }
}
