package es.upm.dam2016g6.shout.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import es.upm.dam2016g6.shout.support.Utils;

/**
 * Created by pthalamy on 28/11/16.
 */

@IgnoreExtraProperties
public class User {
    private static final String TAG = "TAG_User";
    private static User currentUser = null;

    public String uid;
    public String name;
    public String facebookId;
    public Map<String, Boolean> userChatroomsUids = new HashMap<>();

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

    public Map<String, Boolean> getUserChatroomsUids() {
        return userChatroomsUids;
    }

    @Exclude
    public boolean isCurrentUser() {
        return this.uid == Utils.getCurrentUserUid();
    }
}