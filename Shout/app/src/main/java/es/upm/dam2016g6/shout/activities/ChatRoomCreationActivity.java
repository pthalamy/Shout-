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

import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.ChatRoom;
import es.upm.dam2016g6.shout.support.Utils;

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
    private int range = 100;
    private int ttl = 1;

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
                switch(progress) {
                    case 0: // 100m
                        range = 100;
                        tv_range.setText(Integer.toString(range)+ "m");
                        break;
                    case 1: // 250m
                        range = 250;
                        tv_range.setText(Integer.toString(range)+ "m");
                    break;
                    case 2: // 500m
                        range = 500;
                        tv_range.setText(Integer.toString(range)+ "m");
                    break;
                    case 3: // 1km
                        range = 1000;
                        tv_range.setText(Integer.toString(range / 1000)+ "km");
                    break;
                    case 4: // 2km
                        range = 2000;
                        tv_range.setText(Integer.toString(range / 1000)+ "km");
                    break;
                    case 5: // 3km
                        range = 3000;
                        tv_range.setText(Integer.toString(range / 1000)+ "km");
                    break;
                    case 6: // 5km
                        range = 5000;
                        tv_range.setText(Integer.toString(range / 1000)+ "km");
                    break;
                    case 7: // 10km
                        range = 10000;
                        tv_range.setText(Integer.toString(range / 1000)+ "km");
                    break;
                    case 8: // 15km
                        range = 15000;
                        tv_range.setText(Integer.toString(range / 1000)+ "km");
                    break;
                    case 9: // 25km
                        range = 25000;
                        tv_range.setText(Integer.toString(range / 1000)+ "km");
                    break;
                    default: Log.w(TAG, "Range -> OnProgressChange: Progress value out of range!!!");
                        return;
                }

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
                                switch(progress) {
                    case 0: // 1h
                        ttl = 1;
                        tv_ttl.setText(Integer.toString(ttl) + "h");
                        break;
                    case 1: // 2h
                        ttl = 2;
                        tv_ttl.setText(Integer.toString(ttl) + "h");
                    break;
                    case 2: // 3h
                        ttl = 3;
                        tv_ttl.setText(Integer.toString(ttl) + "h");
                    break;
                    case 3: // 6h
                        ttl = 6;
                        tv_ttl.setText(Integer.toString(ttl) + "h");
                    break;
                    case 4: // 12h
                        ttl = 12;
                        tv_ttl.setText(Integer.toString(ttl) + "h");
                    break;
                    case 5: // 24h
                        ttl = 24;
                        tv_ttl.setText(Integer.toString(ttl) + "h");
                    break;
                    case 6: // 48h
                        ttl = 48;
                        tv_ttl.setText(Integer.toString(ttl) + "h");
                    break;
                    case 7: // 3d
                        ttl = 72;
                        tv_ttl.setText(Integer.toString(ttl / 24) + "d");
                    break;
                    case 8: // 5d
                        ttl = 120;
                        tv_ttl.setText(Integer.toString(ttl / 24) + "d");
                    break;
                    case 9: // 7d
                        ttl = 168;
                        tv_ttl.setText(Integer.toString(ttl / 24) + "d");
                    break;
                    default: Log.w(TAG, "TTL -> OnProgressChange: Progress value out of range!!!");
                        return;
                }
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
            GeoLocation loc = Utils.getCurrentLocation(this);
            ChatRoom chatroom = ChatRoom.writeNewChatRoom(et_title.getText().toString(), category, null,
                    range, ttl, FirebaseAuth.getInstance().getCurrentUser().getUid(), loc);

            Intent upIntent = NavUtils.getParentActivityIntent(ChatRoomCreationActivity.this);
            upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_NEARBY);

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
                upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_NEARBY);
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
                upIntent.putExtra(MainActivity.FRAGMENT_TO_INFLATE, MainActivity.CHATROOMS_FRAGMENT_NEARBY);
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
