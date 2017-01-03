package es.upm.dam2016g6.shout.support;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.Query;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.activities.ProfileActivity;
import es.upm.dam2016g6.shout.model.User;

/**
 * Created by pthalamy on 3/1/17.
 */
public class UsersIndexRecyclerViewAdapter extends FirebaseIndexRecyclerAdapter<User, UserListViewHolder>{
    private static final String TAG = "TAG_" + UsersIndexRecyclerViewAdapter.class.getSimpleName();
    private int modelLayout;

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param keyRef          The Firebase location containing the list of keys to be found in {@code dataRef}.
     *                        Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param dataRef         The Firebase location to watch for data changes.
     *                        Each key key found at {@code keyRef}'s location represents a list item in the {@code RecyclerView}.
     */
    public UsersIndexRecyclerViewAdapter(Class<User> modelClass, int modelLayout, Class<UserListViewHolder> viewHolderClass, Query keyRef, Query dataRef) {
        super(modelClass, modelLayout, viewHolderClass, keyRef, dataRef);
        this.modelLayout = modelLayout;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(modelLayout, parent, false);

        UserListViewHolder vh = new UserListViewHolder(v, new UserListViewHolder.UserListViewHolderClickListener() {
            @Override
            public void onUserSelected(View caller, int position) {
                Log.d(TAG, "OnClick: onUserSelected");
                User user = UsersIndexRecyclerViewAdapter.this.getItem(position);
                Intent intent = new Intent(caller.getContext(), ProfileActivity.class);
                intent.putExtra(ProfileActivity.USER_UID, user.uid);
                caller.getContext().startActivity(intent);
            }
        });

        return vh;
    }
    @Override
    protected void populateViewHolder(UserListViewHolder viewHolder, User user, int position) {
        // Glide profile pic in
        Glide.with(viewHolder.mContext)
                .load(Utils.getFacebookProfilePictureFromID(user.facebookId))
                .placeholder(R.drawable.ic_shoutlogo)
                .into(viewHolder.civ_userPic);

        viewHolder.tv_userName.setText(user.name);
    }
}
