package es.upm.dam2016g6.shout.support;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.Date;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.ChatRoom;

/**
 * Created by pthalamy on 11/12/16.
 */


public class ChatRoomsIndexRecyclerViewAdapter extends FirebaseIndexRecyclerAdapter<ChatRoom, ListChatRoomViewHolder> {
    private static final String TAG = "TAG_" + ChatRoomsIndexRecyclerViewAdapter.class.getSimpleName();

    public ChatRoomsIndexRecyclerViewAdapter(Class<ChatRoom> modelClass, int modelLayout, Class<ListChatRoomViewHolder> viewHolderClass,
                                             Query keyRef, Query dataRef) {
        super(modelClass, modelLayout, viewHolderClass, keyRef, dataRef);
    }

    @Override
    protected void populateViewHolder(ListChatRoomViewHolder viewHolder, ChatRoom chatroom, int position) {
        viewHolder.tv_title.setText(chatroom.title);
        viewHolder.tv_category.setText(chatroom.category);

        // Get number of hours from creation and until expiration
        Date now = new Date();
        long numHoursSinceCreation = (now.getTime() - chatroom.creationDate.getTime()) / (1000 * 3600);
        long numHoursToExpiration = (chatroom.expirationDate.getTime() - now.getTime()) / (1000 * 3600);
        viewHolder.tv_createdOn.setText("Created " + numHoursSinceCreation + "h ago");
        viewHolder.tv_expiresIn.setText("Expires in " + numHoursToExpiration + "h");

        viewHolder.tv_latestTextAuthor.setText(chatroom.lastTextAuthor);
        viewHolder.tv_latestTextTime.setText(chatroom.lastTextTime != null ?
                chatroom.lastTextTime.toLocaleString() : "");

        // TODO: 30/12/16 Set maximum length for latest text preview
        viewHolder.tv_latestText.setText(chatroom.lastText);

        // Glide thumbnail in
        Glide.with(viewHolder.context)
                .load(chatroom.imageUrl)
                .placeholder(R.drawable.ic_shoutlogo)
                .into(viewHolder.iv_thumbnail);

        // TODO: 30/12/16 Handle starring
        //        viewHolder.ib_star.setBackground();
    }

}