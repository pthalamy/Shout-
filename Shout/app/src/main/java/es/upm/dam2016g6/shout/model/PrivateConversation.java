package es.upm.dam2016g6.shout.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pthalamy on 4/1/17.
 */

@IgnoreExtraProperties
public class PrivateConversation {
    private static final String TAG = "TAG_" + PrivateConversation.class.getSimpleName();

    public String uid;
    public String lastText;
    public String lastTextAuthor;
    public long lastTextTime;
    public Map<String, Boolean> users = new HashMap<>(); // uids of users in conversation

    public PrivateConversation() {
        // Default constructor required for calls to DataSnapshot.getValue(PrivateConversation.class)
    }

    public PrivateConversation(String uid, String userId1, String userId2) {
        this.uid = uid;
        this.lastText = "No messages yet.";
        this.lastTextAuthor = "";
        this.lastTextTime = 0;
        users.put(userId1, true);
        users.put(userId2, true);
    }
}
