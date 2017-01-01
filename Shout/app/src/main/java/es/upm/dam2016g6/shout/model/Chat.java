package es.upm.dam2016g6.shout.model;

/**
 * Created by pthalamy on 23/12/16.
 */

public class Chat {
    private String name;
    private String text;
    private String uid;

    public Chat() {
    }

    public Chat(String name, String uid, String message) {
        this.name = name;
        this.text = message;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }
}
