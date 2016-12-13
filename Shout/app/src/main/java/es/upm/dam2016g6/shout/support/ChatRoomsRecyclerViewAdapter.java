package es.upm.dam2016g6.shout.support;

import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.Date;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.ChatRoom;

/**
 * Created by pthalamy on 11/12/16.
 */

public class ChatRoomsRecyclerViewAdapter extends FirebaseRecyclerAdapter<ChatRoom, ChatRoomViewHolder> {

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public ChatRoomsRecyclerViewAdapter(Class<ChatRoom> modelClass, int modelLayout, Class<ChatRoomViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }


    @Override
    protected void populateViewHolder(ChatRoomViewHolder viewHolder, ChatRoom chatroom, int position) {
        // Set title and categories
        viewHolder.tv_title.setText(chatroom.title);
        viewHolder.tv_category.setText(chatroom.category);

        // Get number of hours from creation and until expiration
        Date now = new Date();
        long numHoursSinceCreation = (now.getTime() - chatroom.creationDate.getTime()) / (1000 * 3600);
        long numHoursToExpiration = (chatroom.expirationDate.getTime() - now.getTime()) / (1000 * 3600);
        viewHolder.tv_created.setText("Created " + numHoursSinceCreation + "h ago");
        viewHolder.tv_expires.setText("Expires in " + numHoursToExpiration + "h");

        // Glide image in
        Glide.with(viewHolder.context)
                .load(chatroom.imageUrl)
                .placeholder(R.drawable.ic_shoutlogo)
                .into(viewHolder.imageView);

        // TODO: 12/12/16 Implement ChatRoom Sharing
        viewHolder.bt_share.setEnabled(false);
        viewHolder.bt_share.setAlpha(0.5f);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!chatroom.usersUids.containsKey(userUid)) {
            viewHolder.bt_join.setTag(chatroom);
            viewHolder.bt_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button bt = (Button)view;
                    // Retrieve previously stored tag
                    ChatRoom chatroom = (ChatRoom) view.getTag();
                    DatabaseReference ref = Utils.getDatabase().getReference();
                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ref.child("/users/" + userUid + "/userChatroomsUids/" + chatroom.uid).setValue(true);
                    ref.child("/chatrooms/" + chatroom.uid + "/userUids/" + userUid).setValue(true);
                    bt.setEnabled(false);
                    bt.setAlpha(0.5f);
                    bt.setText("JOINED");
                    bt.setTextSize(TypedValue.COMPLEX_UNIT_PX, bt.getTextSize() - 2);

                }
            });
        } else { // User already in chatroom, cannot join again
            viewHolder.bt_join.setEnabled(false);
            viewHolder.bt_join.setAlpha(0.5f);
            viewHolder.bt_join.setText("JOINED");
            viewHolder.bt_join.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewHolder.bt_join.getTextSize() - 2);
        }

    }
}
