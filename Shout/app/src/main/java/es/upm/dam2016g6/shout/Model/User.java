package es.upm.dam2016g6.shout.model;

import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterItem;

import java.util.HashMap;
import java.util.Map;

import es.upm.dam2016g6.shout.support.Utils;

/**
 * Created by pthalamy on 28/11/16.
 */

@IgnoreExtraProperties
public class User implements ClusterItem {
    private static final String TAG = "TAG_" + User.class.getSimpleName();
    private static User currentUser = null;

    public String uid;
    public String name;
    public String facebookId;
    public ShoutLocation location;
//    public LatLng location;
    public Map<String, Boolean> chatrooms = new HashMap<>();
    public Map<String, Boolean> friends = new HashMap<>(); // index to friends as uids
    // index to private conversations as follows: <chatUid, contactUid>
    public Map<String, String> privateChats = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String name, String facebookId) {
        this.uid = uid;
        this.name = name;
        this.facebookId = facebookId;
    }

    @Exclude
    public static User writeNewUser(String uid, String name, String facebookId) {
        User user = new User(uid, name, facebookId);

        DatabaseReference ref = Utils.getDatabase().getReference("users");
        ref.child(uid).setValue(user);

        return user;
    }

    // Singleton current user getter, must be called once before use,
    //  Otherwise currentUser will not have been fetched from database
    @Exclude
    public static User getCurrentUser() {
        if (currentUser != null)
            return currentUser;

        DatabaseReference ref = Utils.getDatabase().getReference("/users/");
        Query query = ref.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange:" + dataSnapshot.getKey());
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled:" + databaseError.toException());
            }
        });

        return currentUser;
    }

    @Exclude
    public boolean isCurrentUser() {
        return this.uid.equals(Utils.getCurrentUserUid());
    }

    @Exclude
    public void addFriend(String friendUid) {
        DatabaseReference ref = Utils.getDatabase().getReference();
        ref.child("/users/" + this.uid + "/friends/" + friendUid).setValue(true);
    }

    @Exclude
    public void removeFriend(String friendUid) {
        DatabaseReference ref = Utils.getDatabase().getReference();
        ref.child("/users/" + this.uid + "/friends/" + friendUid).removeValue();
    }

    @Exclude
    public String createConversationWithUser(String userUid) {
        DatabaseReference ref = Utils.getDatabase().getReference();
        String conversationKey = ref.child("/privateConversations/").push().getKey();
        PrivateConversation pc = new PrivateConversation(conversationKey, this.uid, userUid);

        ref.child("/privateConversations/" + conversationKey).setValue(pc);

        // Create chat for both users
        ref.child("/users/" + this.uid + "/privateChats/" + conversationKey).setValue(userUid);
        ref.child("/users/" + userUid + "/privateChats/" + conversationKey).setValue(this.uid);

        return conversationKey; // Return the chat's key to access it easily in caller
    }

    @Exclude
    public void deleteConversationWithUser(String convUid, String userUid) {
        DatabaseReference ref = Utils.getDatabase().getReference();
        // Delete chat for both user, for the lulz
        ref.child("/users/" + this.uid + "/privateChats/" + convUid).removeValue();
        ref.child("/users/" + userUid + "/privateChats/" + convUid).removeValue();
        ref.child("/privateConversations/" + convUid).removeValue();
        ref.child("/messages/" + convUid).removeValue();
    }


    @Override
    @Exclude
    public LatLng getPosition() {
        if (this.location == null) {
            GeoLocation loc = Utils.getCurrentLocation(null);
            return new LatLng(loc.latitude, loc.longitude);
        }

        return new LatLng(location.latitude, location.longitude);
    }
}