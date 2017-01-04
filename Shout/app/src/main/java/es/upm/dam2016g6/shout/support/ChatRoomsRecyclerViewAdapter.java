package es.upm.dam2016g6.shout.support;

import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
        viewHolder.tv_distance.setText(Utils.getUserDistanceToLatLng(
                chatroom.location.latitude,
                chatroom.location.longitude));
        viewHolder.tv_range.setText(Utils.distanceInMetersToString(chatroom.range));

        // Get number of hours from creation and until expiration
        Date now = new Date();
        long numHoursSinceCreation = (now.getTime() - chatroom.creationDate) / (1000 * 3600);
        long numHoursToExpiration = (chatroom.expirationDate- now.getTime()) / (1000 * 3600);

        if (numHoursSinceCreation < 72)
            viewHolder.tv_created.setText("Created " + numHoursSinceCreation + "h ago");
        else
            viewHolder.tv_created.setText("Created " + numHoursSinceCreation / 24 + "d ago");

        if (numHoursToExpiration < 72)
            viewHolder.tv_expires.setText("Expires in " + numHoursToExpiration + "h");
        else
            viewHolder.tv_expires.setText("Expires in " + numHoursToExpiration / 24 + "d");

        // Glide image in
        Glide.with(viewHolder.context)
                .load(chatroom.imageUrl)
                .placeholder(R.drawable.ic_shoutlogo)
                .into(viewHolder.imageView);

        // TODO: 12/12/16 Implement ChatRoom Sharing
        viewHolder.bt_share.setEnabled(false);
        viewHolder.bt_share.setAlpha(0.5f);

        viewHolder.bt_join.setTag(chatroom);
        String userUid = Utils.getCurrentUserUid();
        if (!chatroom.members.containsKey(userUid)) {
            setButtonJoin(viewHolder.bt_join);
        } else { // User already in chatroom, cannot join again
            setButtonLeave(viewHolder.bt_join);
        }
    }

    public static void setButtonJoin(Button bt) {
        bt.setText("JOIN");
        bt.setBackgroundColor(bt.getResources().getColor(R.color.colorPrimary));
                bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button bt = (Button)view;
                ChatRoom chatroom = (ChatRoom) view.getTag();
                ChatRoom.joinChatroom(chatroom);
                setButtonLeave(bt);
            }
        });
    }

    public static void setButtonLeave(Button bt) {
        bt.setText("LEAVE");
        bt.setBackgroundColor(bt.getResources().getColor(R.color.colorCancelRed));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button bt = (Button)view;
                ChatRoom chatroom = (ChatRoom) view.getTag();
                ChatRoom.leaveChatroom(chatroom);
                setButtonJoin(bt);
            }
        });
    }

}