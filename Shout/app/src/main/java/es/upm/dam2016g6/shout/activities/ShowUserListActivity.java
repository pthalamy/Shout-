package es.upm.dam2016g6.shout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.User;
import es.upm.dam2016g6.shout.support.UserListViewHolder;
import es.upm.dam2016g6.shout.support.UsersIndexRecyclerViewAdapter;

public class ShowUserListActivity extends AppCompatActivity {
    private static final String TAG = "TAG_" + ShowUserListActivity.class.getSimpleName();
    public static final String REF_PATH = "USERLIST_INTENT_REFPATH";
    public static final String NUM_MEMBERS = "USERLIST_INTENT_NUMMEMBERS";

    private String refPath;
    private DatabaseReference keyRef;
    private Toolbar mToolbar;
    private TextView tv_numMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_list);

        // Parse intent
        Intent intent = getIntent();
        refPath = intent.getStringExtra(REF_PATH);
        int numMembers = intent.getIntExtra(NUM_MEMBERS, 2);

        // Add toolbar to activity
        mToolbar = (Toolbar) findViewById(R.id.toolbar_userlist);
        mToolbar.setTitle("Chat Room Members");
        this.setSupportActionBar(mToolbar);

        // Store users into a recycler view and configure it
        RecyclerView rv = (RecyclerView) this.findViewById(R.id.rv_users);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        keyRef = FirebaseDatabase
                .getInstance()
                .getReference(refPath);

        DatabaseReference dataRef = FirebaseDatabase
                .getInstance()
                .getReference("/users/");

        UsersIndexRecyclerViewAdapter adapter = new UsersIndexRecyclerViewAdapter(
                User.class,
                R.layout.list_user_item_layout,
                UserListViewHolder.class,
                keyRef,
                dataRef);

        rv.setAdapter(adapter);
        tv_numMembers = (TextView) findViewById(R.id.ul_tv_numberUsers);
        tv_numMembers.setText("Number of members: " + Integer.toString(numMembers));
    }
}
