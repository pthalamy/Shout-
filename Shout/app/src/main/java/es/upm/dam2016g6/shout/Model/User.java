package es.upm.dam2016g6.shout.Model;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by pthalamy on 28/11/16.
 */

@IgnoreExtraProperties
public class User {

    private static User userMe;

    private String userId;
    private String name;
    private GeoLocation location = null;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static User addNewUser(String userId, String name) {
        User user = new User(userId, name);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(userId).setValue(user);

        return user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }
}