package es.upm.dam2016g6.shout.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.ChatRoom;

public class ChatRoomCreationActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "TAG_CRCActivity";

    private EditText et_title;
    private Spinner sp_categories;
    private ImageButton ib_thumbnailPicker;
    private TextView tv_range;
    private TextView tv_ttl;
    private SeekBar sb_range;
    private SeekBar sb_ttl;

    private Image thumbnail = null;
    private String category = "";
    private int range = 0;
    private int ttl = 0;

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

        // Initialize member variables
        et_title = (EditText) findViewById(R.id.tf_crc_title);
        sp_categories = (Spinner) findViewById(R.id.sp_crc_categories);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sp_categories.setAdapter(adapter);
        sp_categories.setOnItemSelectedListener(this);
        ib_thumbnailPicker = (ImageButton) findViewById(R.id.ib_crc_thumbnail);
        // TODO: 5/12/16 HANDLE IMAGE UPLOAD
        tv_range = (TextView) findViewById(R.id.tv_crc_range_count);
        tv_ttl = (TextView) findViewById(R.id.tv_crc_ttl_count);

        sb_range = (SeekBar) findViewById(R.id.sb_crc_range);
        sb_range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                ChatRoomCreationActivity.this.range = progress;
                tv_range.setText(Integer.toString(range) + "m");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sb_ttl = (SeekBar) findViewById(R.id.sb_crc_ttl);
        sb_ttl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                ChatRoomCreationActivity.this.ttl = progress;
                tv_ttl.setText(Integer.toString(ttl) + "h");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void createChatRoomIfValid(View view) {
        Log.d(TAG, "onClick: createChatRoomIfValid");
        if (creationFormIsComplete()) {
            // Create chat room
            ChatRoom chatroom = ChatRoom.writeNewChatRoom(et_title.getText().toString(), category, null,
                    range, ttl, FirebaseAuth.getInstance().getCurrentUser().getUid());

            Intent upIntent = NavUtils.getParentActivityIntent(ChatRoomCreationActivity.this);
            upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_CREATION);

            finish();
        } else {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 2. Chain together various setter methods to set the dialog characteristics
            // Add the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setMessage(R.string.crc_form_incomplete)
                    .setTitle(R.string.crc_form_incomplete_title);
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void cancelCreation(View view) {
        Log.d(TAG, "onClick: cancelCreation");
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Chain together various setter methods to set the dialog characteristics
        // Add the buttons
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent upIntent = NavUtils.getParentActivityIntent(ChatRoomCreationActivity.this);
                upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_CREATION);
                ChatRoomCreationActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setMessage(R.string.crc_cancel_dialog_message)
                .setTitle(R.string.crc_cancel_dialog_title);
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_CREATION);
                NavUtils.navigateUpTo(this, upIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean creationFormIsComplete() {
        return !et_title.getText().equals("")
                && !category.equals("")
                && ttl != 0
                && range != 0;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        category = (String) parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        category = "";
    }
}
