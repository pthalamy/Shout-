package es.upm.dam2016g6.shout.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import es.upm.dam2016g6.shout.R;

public class ChatRoomCreation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_creation);

        // my_child_toolbar is defined in the layout file
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_chat_room_creation);
        mToolbar.setTitle("Create Chat Room");
        setSupportActionBar(mToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }


}
