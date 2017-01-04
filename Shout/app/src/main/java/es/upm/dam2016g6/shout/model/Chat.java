package es.upm.dam2016g6.shout.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by pthalamy on 23/12/16.
 */

@IgnoreExtraProperties
public class Chat {
    public String name;
    public String text;
    public String userPicUrl;
    public long timestamp;

    public Chat() {
    }

    public Chat(String name, String message, String userPicUrl) {
        this.name = name;
        this.text = message;
        this.userPicUrl = userPicUrl;
        this.timestamp = new Date().getTime();
    }

    @Exclude
    public boolean isMine() {
        // TODO: 3/1/17 Could be done in a safer manner, see later
        String myUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        return this.name.equals(myUsername);
    }
}
