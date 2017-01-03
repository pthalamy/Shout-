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
import es.upm.dam2016g6.shout.model.User;
import es.upm.dam2016g6.shout.support.ChatHolder;
import es.upm.dam2016g6.shout.support.ChatRecyclerViewAdapter;
import es.upm.dam2016g6.shout.support.Utils;

public class ChatActivity extends AppCompatActivity {

    public static final String CHAT_UID = "CHAT_INTENT_UID";
    public static final String CHAT_TARGET = "CHAT_INTENT_TARGET";
    public static final String CHAT_TARGET_CHATROOM = "CHAT_INTENT_TARGET_CHATROOM";
    public static final String CHAT_TARGET_USER = "CHAT_INTENT_TARGET_USER";
    private static final String TAG = "TAG_" + ChatActivity.class.getSimpleName();

    private ChatRecyclerViewAdapter mAdapter;
    private DatabaseReference mChatRef;
    private DatabaseReference mTargetRef;
    private Toolbar mToolbar;
    private EditText et_chat_message;


    // Passed as intent args:
    private String chatUid; // Uid of this chat
    private ChatRoom chatroom; // Reference to the current chatroom if this is a chatroom conversation
    private User friend; // Friend with whom we are currently chatting
    private String chatTarget; /* Activity is shared between chat rooms and private chats,
                                   this field indicates for what it is being used */
    // end passed as intent args

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Parse intent
        Intent intent = getIntent();
        chatUid = intent.getStringExtra(CHAT_UID);
        chatTarget = intent.getStringExtra(CHAT_TARGET);
        if (chatTarget.equals(CHAT_TARGET_CHATROOM)) {
            mTargetRef= Utils.getDatabase().getReference("/chatrooms/" + chatUid);
            mTargetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatroom = dataSnapshot.getValue(ChatRoom.class);
                    mToolbar.setTitle(chatroom.title);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "ChatActivity: onCreate: Could not retrieve chatroom information");
                }
            });
        } else {
            // Do something
        }

        // Add toolbar to activity
        mToolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        mToolbar.setTitle("Chat");
        this.setSupportActionBar(mToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Store chats into a recycler view and configure it
        final RecyclerView rv = (RecyclerView) this.findViewById(R.id.rv_chat_messages);
        rv.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        this.mChatRef = FirebaseDatabase
                .getInstance()
                .getReference("/messages/" + chatUid);

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

        if (chatTarget.equals(CHAT_TARGET_CHATROOM)) {
            // Update Chatroom's last messages attributes
            chatroom.lastTextTime = chat.timestamp;
            chatroom.lastTextAuthor = chat.name;
            chatroom.lastText = messagePreview;
            mTargetRef.setValue(chatroom);
        }

        et_chat_message.setText("");
    }

    // Toolbar and Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (this.chatTarget.equals(CHAT_TARGET_CHATROOM))
            this.getMenuInflater().inflate(R.menu.chatroom_chat_menu, menu);
//        else
//            this.getMenuInflater().inflate(R.menu.chatroom_chat_menu, menu);

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
                if (chatTarget.equals(CHAT_TARGET_CHATROOM))
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
                        ChatRoom.leaveChatroom(chatroom);
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
                Intent intent = new Intent(this, ShowUserListActivity.class);
                String keyRef = "/chatrooms/" + chatroom.uid + "/userUids/";
                intent.putExtra(ShowUserListActivity.REF_PATH, keyRef);
                intent.putExtra(ShowUserListActivity.NUM_MEMBERS, chatroom.userUids.size());
                this.startActivity(intent);

                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
