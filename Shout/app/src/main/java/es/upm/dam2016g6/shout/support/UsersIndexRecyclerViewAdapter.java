package es.upm.dam2016g6.shout.support;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.activities.ChatActivity;
import es.upm.dam2016g6.shout.activities.ProfileActivity;
import es.upm.dam2016g6.shout.model.User;

import static es.upm.dam2016g6.shout.activities.ShowUserListActivity.TARGET_CREATECONVO;
import static es.upm.dam2016g6.shout.activities.ShowUserListActivity.TARGET_CRMEMBERS;
import static es.upm.dam2016g6.shout.activities.ShowUserListActivity.TARGET_FRIENDLIST;

/**
 * Created by pthalamy on 3/1/17.
 */
public class UsersIndexRecyclerViewAdapter extends FirebaseIndexRecyclerAdapter<User, UserListViewHolder>{
    private static final String TAG = "TAG_" + UsersIndexRecyclerViewAdapter.class.getSimpleName();
    private int mModelLayout;
    private String mTarget;

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
    public UsersIndexRecyclerViewAdapter(Class<User> modelClass, int modelLayout, Class<UserListViewHolder> viewHolderClass, Query keyRef, Query dataRef, String target) {
        super(modelClass, modelLayout, viewHolderClass, keyRef, dataRef);
        this.mModelLayout = modelLayout;
        this.mTarget = target;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
        UserListViewHolder vh = null;

        switch (mTarget) {
            case TARGET_CREATECONVO:
                vh = new UserListViewHolder(v, new UserListViewHolder.UserListViewHolderClickListener() {
                    @Override
                    public void onUserSelected(View caller, int position) {
                        Log.d(TAG, "OnClick: onUserSelected");
                        final User user = UsersIndexRecyclerViewAdapter.this.getItem(position);
                        final View callerF = caller;
                        final DatabaseReference mCurrentUserRef= Utils.getDatabase().getReference("/users/" + Utils.getCurrentUserUid());
                        mCurrentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User currentUser = dataSnapshot.getValue(User.class);

                                String chatUid;
                                if (currentUser.privateChats.containsValue(user.uid)) { // Chat between the two already exists
                                    chatUid = Utils.getKeyByValue(currentUser.privateChats, user.uid); // Costly, no other way?
                                } else { // Does not exist, create chat first and then segue to it
                                    chatUid = currentUser.createConversationWithUser(user.uid);
                                }

                                Intent intent = new Intent(callerF.getContext(), ChatActivity.class);
                                intent.putExtra(ChatActivity.CHAT_UID, chatUid);
                                intent.putExtra(ChatActivity.CHAT_TARGET, ChatActivity.CHAT_TARGET_USER);
                                intent.putExtra(ChatActivity.CHAT_USER_UID, user.uid);
                                callerF.getContext().startActivity(intent);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "onUserSelected: Could not retrieve current user information");
                            }
                        });
                    }
                });
                break;
            case TARGET_CRMEMBERS:
                vh = new UserListViewHolder(v, new UserListViewHolder.UserListViewHolderClickListener() {
                    @Override
                    public void onUserSelected(View caller, int position) {
                        Log.d(TAG, "OnClick: onUserSelected");
                        User user = UsersIndexRecyclerViewAdapter.this.getItem(position);
                        Intent intent = new Intent(caller.getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.USER_UID, user.uid);
                        caller.getContext().startActivity(intent);
                    }
                });
                break;
            case TARGET_FRIENDLIST:
                vh = new UserListViewHolder(v, new UserListViewHolder.UserListViewHolderClickListener() {
                    @Override
                    public void onUserSelected(View caller, int position) {
                        Log.d(TAG, "OnClick: onUserSelected:");
                        User user = UsersIndexRecyclerViewAdapter.this.getItem(position);
                        Intent intent = new Intent(caller.getContext(), ProfileActivity.class);
                        intent.putExtra(ProfileActivity.USER_UID, user.uid);
                        caller.getContext().startActivity(intent);
                    }
                });

                break;
            default:
                throw new UnsupportedOperationException(UsersIndexRecyclerViewAdapter.class.getSimpleName()
                        + ": onCreateViewHolder: unknown target type");
        }

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
