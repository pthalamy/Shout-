package es.upm.dam2016g6.shout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private User mCurrentUser;

    private CircleImageView civ_profilePic;
    private TextView tv_userName;
    private RecyclerView rv_commonLikes;
    private Button bt_addFriend;
    private Button bt_sendMessage;

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

        // Set up components
        civ_profilePic = (CircleImageView) findViewById(R.id.profile_civ_profilePic);
        tv_userName = ((TextView) findViewById(R.id.profile_tv_name));
        rv_commonLikes = (RecyclerView) this.findViewById(R.id.profile_rv_commonLikes);
        bt_addFriend = (Button) this.findViewById(R.id.profile_bt_friendList);
        bt_sendMessage = (Button) this.findViewById(R.id.profile_bt_sendMessage);

        mUserRef = Utils.getDatabase().getReference("");
    }

    public void loadUserData() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String profileImgUrl = Utils.getFacebookProfilePictureFromID(user.facebookId);

        // Load profile picture from Firebase
        Glide.with(this)
                .load(profileImgUrl)
                .into(civ_profilePic);

        tv_userName.setText(user.name);
        // Store likes into a recycler view and configure it
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        rv_commonLikes.setLayoutManager(layoutManager);
        rv_commonLikes.setHasFixedSize(true);

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

        // Configure action buttons and set up callbacks
        if (user.isCurrentUser()) { // Disable buttons and no need to set any callback
            bt_addFriend.setAlpha(0.5f);
            bt_addFriend.setEnabled(false);
            bt_sendMessage.setAlpha(0.5f);
            bt_sendMessage.setEnabled(false);
        } else {
            // Set callbacks asynchronously,
            //  since we need to fetch the information of the current user first
            final DatabaseReference mCurrentUserRef= Utils.getDatabase().getReference("/users/" + Utils.getCurrentUserUid());
            mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCurrentUser = dataSnapshot.getValue(User.class);
                    if (mCurrentUser.friends.containsKey(user.uid)) { // Current user already has other user in friendlist
                        setButtonRemove();
                    } else { // They are not friends yet
                        setButtonAdd();
                    }

                    if (mCurrentUser.privateChats.containsValue(user.uid)) { // Chat between the two already exists
                        bt_sendMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Segue to conversation between the two
                                String chatUid = Utils.getKeyByValue(mCurrentUser.privateChats, user.uid); // Costly, no other way?
                                segueToChat(chatUid);
                            }
                        });
                    } else { // Does not exist, create chat first and then segue to it
                        bt_sendMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Segue to conversation between the two
                                String chatUid = mCurrentUser.createConversationWithUser(user.uid);
                                segueToChat(chatUid);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "ProfileActivity: loadUserData: Could not retrieve current user information");
                }
            });
        }

    }

    public void segueToChat(String chatUid) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_UID, chatUid);
        intent.putExtra(ChatActivity.CHAT_TARGET, ChatActivity.CHAT_TARGET_USER);
        intent.putExtra(ChatActivity.CHAT_USER_UID, userUid);
        this.startActivity(intent);
    }

    private void initiateLikesComparison() {
        fbCommonLikes =  new ArrayList<FacebookLike>();
        for (FacebookLike like : currentUserLikes) {
            if (otherUserLikes.contains(like))
                fbCommonLikes.add(like);
        }

        MyLikesRecyclerViewAdapter adapter = new MyLikesRecyclerViewAdapter(fbCommonLikes);
        rv_commonLikes.setAdapter(adapter);
    }

    public void setButtonAdd() {
        bt_addFriend.setText("ADD FRIEND");
        bt_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentUser.addFriend(user.uid);
                setButtonRemove();
            }
        });
    }

    public void setButtonRemove() {
        bt_addFriend.setText("REMOVE FRIEND");
        bt_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentUser.removeFriend(user.uid);
                setButtonAdd();
            }
        });

    }
}
