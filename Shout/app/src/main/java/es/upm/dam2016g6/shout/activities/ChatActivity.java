package es.upm.dam2016g6.shout.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.Chat;
import es.upm.dam2016g6.shout.model.ChatRoom;
import es.upm.dam2016g6.shout.model.PrivateConversation;
import es.upm.dam2016g6.shout.model.User;
import es.upm.dam2016g6.shout.support.ChatHolder;
import es.upm.dam2016g6.shout.support.ChatRecyclerViewAdapter;
import es.upm.dam2016g6.shout.support.Utils;

public class ChatActivity extends AppCompatActivity {

    public static final String CHAT_UID = "CHAT_INTENT_UID";
    public static final String CHAT_TARGET = "CHAT_INTENT_TARGET";
    public static final String CHAT_TARGET_CHATROOM = "CHAT_INTENT_TARGET_CHATROOM";
    public static final String CHAT_TARGET_USER = "CHAT_INTENT_TARGET_USER";
    public static final String CHAT_USER_UID = "CHAT_INTENT_USER_UID"; // ID of other user in convo if not chatroom
    private static final String TAG = "TAG_" + ChatActivity.class.getSimpleName();

    private ChatRecyclerViewAdapter mAdapter;
    private DatabaseReference mChatRef;
    private DatabaseReference mTargetRef;
    private DatabaseReference mUserRef;
    private Toolbar mToolbar;
    private EditText et_chat_message;


    // Passed as intent args:
    private String mChatUid; // Uid of this chat
    private ChatRoom mChatroom; // Reference to the current chatroom if this is a chatroom conversation
    private PrivateConversation mPrivateConversation;
    private User mFriend; // Friend with whom we are currently chatting
    private String mFriendUid; // Uid of Friend with whom we are currently chatting
    private String mChatTarget; /* Activity is shared between chat rooms and private chats,
                                   this field indicates for what it is being used */
    // end passed as intent args

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Add toolbar to activity
        mToolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        this.setSupportActionBar(mToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Parse intent
        Intent intent = getIntent();
        mChatUid = intent.getStringExtra(CHAT_UID);
        mChatTarget = intent.getStringExtra(CHAT_TARGET);
        if (mChatTarget.equals(CHAT_TARGET_CHATROOM)) {
            mTargetRef= Utils.getDatabase().getReference("/chatrooms/" + mChatUid);
            mTargetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mChatroom = dataSnapshot.getValue(ChatRoom.class);
                    mToolbar.setTitle(mChatroom.title);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "ChatActivity: onCreate: Could not retrieve chatroom information");
                }
            });
        } else {
            mFriendUid = intent.getStringExtra(CHAT_USER_UID);
            mUserRef = Utils.getDatabase().getReference("/users/" + mFriendUid);
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mFriend = dataSnapshot.getValue(User.class);
                    if (mFriend != null)
                        mToolbar.setTitle("Chat: " + mFriend.name);
                    else // Should never happen though
                        mToolbar.setTitle("Chat");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "ChatActivity: onCreate: Could not retrieve user information");
                }
            });

            mTargetRef = Utils.getDatabase().getReference("/privateConversations/" + mChatUid);
            mTargetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mPrivateConversation = dataSnapshot.getValue(PrivateConversation.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "ChatActivity: onCreate: Could not retrieve private conversation information");
                }
            });
        }

        // Store chats into a recycler view and configure it
        final RecyclerView rv = (RecyclerView) this.findViewById(R.id.rv_chat_messages);
        rv.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        this.mChatRef = FirebaseDatabase
                .getInstance()
                .getReference("/messages/" + mChatUid);

        this.mAdapter = new ChatRecyclerViewAdapter(Chat.class, R.layout.chat_item_layout, ChatHolder.class, mChatRef, this);
        rv.setAdapter(this.mAdapter);

        // Register data observer for auto scrolling to latest message
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mAdapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rv.scrollToPosition(positionStart);
                }
            }
        });


        et_chat_message = (EditText) findViewById(R.id.et_chat_message);
    }

    public void sendMessage(View view) {
        // Retrieve data for message
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String message = et_chat_message.getText().toString();
        String messagePreview = message.length() > 35 ? message.substring(0, 34) : message;
        Chat chat = new Chat(user.getDisplayName(), message, user.getPhotoUrl().toString());

        // Push message to the Firebase Chat
        mChatRef.push().setValue(chat);

        if (mChatTarget.equals(CHAT_TARGET_CHATROOM)) {
            // Update Chatroom's last messages attributes
            mChatroom.lastTextTime = chat.timestamp;
            mChatroom.lastTextAuthor = chat.name;
            mChatroom.lastText = messagePreview;
            mTargetRef.setValue(mChatroom);
        } else {
            // Update Private Conversation's last messages attributes
            mPrivateConversation.lastTextTime = chat.timestamp;
            mPrivateConversation.lastTextAuthor = chat.name;
            mPrivateConversation.lastText = messagePreview;
            mTargetRef.setValue(mPrivateConversation);
        }

        et_chat_message.setText("");
    }

    // Toolbar and Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (this.mChatTarget.equals(CHAT_TARGET_CHATROOM))
            this.getMenuInflater().inflate(R.menu.chatroom_chat_menu, menu);
        else
            this.getMenuInflater().inflate(R.menu.pm_chat_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                if (mChatTarget.equals(CHAT_TARGET_CHATROOM))
                    upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_MYCHATROOMS);
                else // user
                    upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.PRIVATECONVERSATIONS_FRAGMENT);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
            case R.id.action_leave:
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // 2. Chain together various setter methods to set the dialog characteristics
                // Add the buttons
                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        ChatRoom.leaveChatroom(mChatroom);
                        Intent upIntent = NavUtils.getParentActivityIntent(ChatActivity.this);
                        upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_MYCHATROOMS);
                        ChatActivity.this.finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setMessage(R.string.leaveChatroom_cancel_dialog_message)
                        .setTitle(R.string.leaveChatroom_cancel_dialog_title);
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            case R.id.action_showMembers:
                Intent intentSM = new Intent(this, ShowUserListActivity.class);
                String keyRef = "/chatrooms/" + mChatroom.uid + "/userUids/";
                intentSM.putExtra(ShowUserListActivity.REF_PATH, keyRef);
                intentSM.putExtra(ShowUserListActivity.NUM_USERS, mChatroom.userUids.size());
                intentSM.putExtra(ShowUserListActivity.TARGET, ShowUserListActivity.TARGET_CRMEMBERS);
                this.startActivity(intentSM);

                return true;
            case R.id.action_showProfile:
                Intent intentSP = new Intent(this, ProfileActivity.class);
                intentSP.putExtra(ProfileActivity.USER_UID, mFriendUid);
                this.startActivity(intentSP);

                return true;

            case R.id.action_deleteConversation:
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builderDC = new AlertDialog.Builder(this);
                // 2. Chain together various setter methods to set the dialog characteristics
                // Add the buttons
                builderDC.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button

                        final DatabaseReference mCurrentUserRef= Utils.getDatabase().getReference("/users/" + Utils.getCurrentUserUid());
                        mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User currentUser = dataSnapshot.getValue(User.class);

                                // Delete conversation
                                currentUser.deleteConversationWithUser(mChatUid, mFriendUid);

                                //  go back to conversations screen
                                Intent upIntentDC = NavUtils.getParentActivityIntent(ChatActivity.this);
                                upIntentDC.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.PRIVATECONVERSATIONS_FRAGMENT);
                                NavUtils.navigateUpTo(ChatActivity.this, upIntentDC);
                                ChatActivity.this.finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "ChatActivity: deleteConversation: Could not retrieve current user information");
                            }
                        });
                    }
                });
                builderDC.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builderDC.setMessage(R.string.deletePM_dialog_message)
                        .setTitle(R.string.deletePM_dialog_title);

                // 3. Get the AlertDialog from create()
                AlertDialog dialogDC = builderDC.create();
                dialogDC.show();

                return true;

        }


        return super.onOptionsItemSelected(item);
    }
}
