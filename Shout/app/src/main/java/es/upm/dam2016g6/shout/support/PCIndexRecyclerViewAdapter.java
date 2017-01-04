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
import es.upm.dam2016g6.shout.model.PrivateConversation;
import es.upm.dam2016g6.shout.model.User;

/**
 * Created by pthalamy on 4/1/17.
 */

public class PCIndexRecyclerViewAdapter extends FirebaseIndexRecyclerAdapter<PrivateConversation, PrivateConversationViewHolder> {
    private static final String TAG = "TAG_" + PCIndexRecyclerViewAdapter.class.getSimpleName();
    private int mModelLayout;

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
    public PCIndexRecyclerViewAdapter(Class modelClass, int modelLayout, Class viewHolderClass, Query keyRef, Query dataRef) {
        super(modelClass, modelLayout, viewHolderClass, keyRef, dataRef);
        this.mModelLayout = modelLayout;
    }


    @Override
    public PrivateConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);

        PrivateConversationViewHolder vh = new PrivateConversationViewHolder(v, new PrivateConversationViewHolder.PCViewHolderClickListener() {
            @Override
            public void onPCSelected(View caller, int position) {
                Log.d(TAG, "OnClick: onPCSelected");
                PrivateConversation pc = PCIndexRecyclerViewAdapter.this.getItem(position);
                Intent intent = new Intent(caller.getContext(), ChatActivity.class);
                intent.putExtra(ChatActivity.CHAT_UID, pc.uid);
                intent.putExtra(ChatActivity.CHAT_TARGET, ChatActivity.CHAT_TARGET_USER);
                intent.putExtra(ChatActivity.CHAT_USER_UID, pc.getContactUid());
                caller.getContext().startActivity(intent);
            }
        });

        return vh;
    }

    @Override
    protected void populateViewHolder(PrivateConversationViewHolder viewHolder, PrivateConversation pc, int position) {
        final PrivateConversation pcF = pc;
        final PrivateConversationViewHolder viewHolderF = viewHolder;
        DatabaseReference mURef = Utils.getDatabase().getReference("/users/" + pc.getContactUid());
        mURef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User contact = dataSnapshot.getValue(User.class);
                assert (contact != null);

                viewHolderF.tv_title.setText(contact.name);

                viewHolderF.tv_latestTextAuthor.setText(pcF.lastTextAuthor);

                if (pcF.lastTextTime != 0)
                    viewHolderF.tv_latestTextTime.setText(
                            Utils.getDateStringFromTimestamp(pcF.lastTextTime));
                else // no text yet
                    viewHolderF.tv_latestTextTime.setText("");

                viewHolderF.tv_latestText.setText(pcF.lastText);

                String profileImgUrl = Utils.getFacebookProfilePictureFromID(contact.facebookId);

                // Glide thumbnail in
                Glide.with(viewHolderF.context)
                        .load(profileImgUrl)
                        .placeholder(R.drawable.ic_shoutlogo)
                        .into(viewHolderF.civ_profilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "PopulateViewHolder: Could not retrieve pc user information");
            }
        });
    }
}
