package es.upm.dam2016g6.shout.support;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by pthalamy on 5/12/16.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }

}
