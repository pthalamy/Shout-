package es.upm.dam2016g6.shout.model;

import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by pthalamy on 23/12/16.
 */

public class Chat {
    public String name;
    public String text;
    public String uid;
    public Map<String, String> timestamp; // Date of the message as a Long Unix epoch time, autopopulated by the Firebase Server

    public Chat() {
    }

    public Chat(String name, String uid, String message) {
        this.name = name;
        this.text = message;
        this.uid = uid;
        this.timestamp = ServerValue.TIMESTAMP;
    }
}
