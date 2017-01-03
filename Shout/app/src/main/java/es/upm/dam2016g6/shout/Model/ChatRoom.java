package es.upm.dam2016g6.shout.model;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.upm.dam2016g6.shout.support.Utils;

/**
 * Created by pthalamy on 4/12/16.
 */

@IgnoreExtraProperties
public class ChatRoom {

    private static final String TAG = "TAG_" + ChatRoom.class.getSimpleName();
    private static DatabaseReference mChatroomsReference = null;
    private static HashMap<String, ChatRoom> chatrooms = new HashMap<>();
    private static final String defaultImageUrl = "http://shushi168.com/data/out/193/37127382-random-image.png";

    public String uid;
    public String title;
    public String category;
    public String imageUrl;
    public long creationDate;
    public long expirationDate;
    public String creatorUid;
    public String lastText;
    public String lastTextAuthor;
    public long lastTextTime;
    public int range;
    public Map<String, Boolean> userUids = new HashMap<>();

    public ChatRoom() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ChatRoom(String uid, String title, String category, String imageUrl,
                    int range, int ttl, String creatorUid) {
        this.uid = uid;
        this.title = title;
        this.category = category;
        this.imageUrl = imageUrl != null ? imageUrl : defaultImageUrl;
        // Range is in meters
        this.range = range;
        this.creationDate = new Date().getTime();
        // ttl is in hours, hence ttlms = ttl * 3600 * 1000
        this.expirationDate = new Date().getTime() + ttl * 3600 * 1000;
        this.creatorUid = creatorUid;
        this.lastText = "No messages yet.";
        this.lastTextAuthor = "";
        this.lastTextTime = 0;
        userUids.put(creatorUid, true);
    }

    @Exclude
    public static ChatRoom writeNewChatRoom(String title, String category, String imageUrl,
                                            int range, int ttl, String creatorUid, GeoLocation location) {
        String key = getChatroomsReferenceInstance().push().getKey();
        ChatRoom chatroom = new ChatRoom(key, title, category, imageUrl,
                range, ttl, creatorUid);

        Log.d(TAG, "User " + creatorUid + " created new chatroom: " + key);
        DatabaseReference mRef = Utils.getDatabase().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/chatrooms/" + key, chatroom);
        mRef.updateChildren(childUpdates);
        mRef.child("/users/" + creatorUid + "/userChatroomsUids/" + key).setValue(true);

        // Set General Location as well
        GeoFire geoFireChatrooms = new GeoFire(Utils.getDatabase().getReference("chatroomLocations"));
        geoFireChatrooms.setLocation(chatroom.uid, location);

        return chatroom;
    }

    @Exclude
    public static HashMap<String, ChatRoom> getChatRooms() {
        if (getChatroomsReferenceInstance() != null)
            return chatrooms;

        // else we probably cannot connect to Firebase Database
        // Should never happen!
        throw new UnsupportedOperationException();
    }

    // Singleton getter for chatrooms reference
    @Exclude
    public static DatabaseReference getChatroomsReferenceInstance() {
        // if already instantiated, return instance
        if (mChatroomsReference != null)
            return mChatroomsReference;

        // otherwise, initialize instance and configure listener
        mChatroomsReference = Utils.getDatabase().getReference("chatrooms");
        ChildEventListener chatroomsEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new chatroom has been added, add it to the list
                ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);
                String chatroomKey = dataSnapshot.getKey();

                chatrooms.put(chatroomKey, chatroom);

                Log.d(TAG, chatrooms.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A chatroom has been updated, update its values in the list
                ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);
                String chatroomKey = dataSnapshot.getKey();

                chatrooms.put(chatroom.uid, chatroom);

                Log.d(TAG, chatrooms.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A chatroom has been deleted, remove it from the list
                String chatroomKey = dataSnapshot.getKey();

                chatrooms.remove(chatroomKey);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                throw new UnsupportedOperationException();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled:", databaseError.toException());
            }
        };
        mChatroomsReference.addChildEventListener(chatroomsEventListener);

        return mChatroomsReference;
    }

    public static void joinChatroom(ChatRoom chatroom) {
        DatabaseReference ref = Utils.getDatabase().getReference();
        String userUid = Utils.getCurrentUserUid();
        ref.child("/users/" + userUid + "/userChatroomsUids/" + chatroom.uid).setValue(true);
        ref.child("/chatrooms/" + chatroom.uid + "/userUids/" + userUid).setValue(true);
        chatroom.userUids.put(userUid, true);
    }

    public static void leaveChatroom(ChatRoom chatroom) {
        // Retrieve previously stored tag
        DatabaseReference ref = Utils.getDatabase().getReference();
        String userUid = Utils.getCurrentUserUid();
        ref.child("/users/" + userUid + "/userChatroomsUids/" + chatroom.uid).removeValue();
        ref.child("/chatrooms/" + chatroom.uid + "/userUids/" + userUid).removeValue();
        chatroom.userUids.remove(userUid);
    }

}
