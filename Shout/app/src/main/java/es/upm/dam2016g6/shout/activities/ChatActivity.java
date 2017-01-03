package es.upm.dam2016g6.shout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.Chat;
import es.upm.dam2016g6.shout.model.ChatRoom;
import es.upm.dam2016g6.shout.model.User;
import es.upm.dam2016g6.shout.support.ChatHolder;
import es.upm.dam2016g6.shout.support.ChatRecyclerViewAdapter;

public class ChatActivity extends AppCompatActivity {

    public static final String CHAT_UID = "CHAT_INTENT_UID";
    public static final String CHAT_TARGET = "CHAT_INTENT_TARGET";
    public static final String CHAT_TARGET_CHATROOM = "CHAT_INTENT_TARGET_CHATROOM";
    public static final String CHAT_TARGET_USER = "CHAT_INTENT_TARGET_USER";

    private ChatRecyclerViewAdapter mAdapter;
    private DatabaseReference mRef;

    // Passed as intent args:
    private String chatUid; // Uid of this chat
    private ChatRoom chatroom; // Reference to the current chatroom if this is a chatroom conversation
    private User friend; // Friend with whom we are currently chatting
    // end passed as intent args

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Add toolbar to activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        toolbar.setTitle("Chat"); // TODO: 3/1/17 Use intents to set as Chatroom name or Contact name
        this.setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Store chats into a recycler view and configure it
        RecyclerView rv = (RecyclerView) this.findViewById(R.id.rv_chat_messages);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        // Pass as intent parameter
        this.mRef = FirebaseDatabase
                .getInstance()
                .getReference("/messages/" + chatUid);

        this.mAdapter = new ChatRecyclerViewAdapter(Chat.class, R.layout.chat_item_layout, ChatHolder.class, mRef);
        rv.setAdapter(this.mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
