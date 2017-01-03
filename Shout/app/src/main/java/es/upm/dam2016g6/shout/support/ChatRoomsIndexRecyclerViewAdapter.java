package es.upm.dam2016g6.shout.support;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.Query;

import java.util.Date;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.activities.ChatActivity;
import es.upm.dam2016g6.shout.model.ChatRoom;

/**
 * Created by pthalamy on 11/12/16.
 */


public class ChatRoomsIndexRecyclerViewAdapter extends FirebaseIndexRecyclerAdapter<ChatRoom, ListChatRoomViewHolder> {
    private static final String TAG = "TAG_" + ChatRoomsIndexRecyclerViewAdapter.class.getSimpleName();
    private int modelLayout;

    public ChatRoomsIndexRecyclerViewAdapter(Class<ChatRoom> modelClass, int modelLayout, Class<ListChatRoomViewHolder> viewHolderClass,
                                             Query keyRef, Query dataRef) {
        super(modelClass, modelLayout, viewHolderClass, keyRef, dataRef);
        this.modelLayout = modelLayout;
    }

   @Override
   public ListChatRoomViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(modelLayout, parent, false);

       ListChatRoomViewHolder vh = new ListChatRoomViewHolder(v, new ListChatRoomViewHolder.MyViewHolderClickListener() {
           @Override
           public void onStar(ImageButton callerImageButton, int position) {
               Log.d(TAG, "OnClick: onStar");
               // TODO: 3/1/17 implement starring
           }

           @Override
           public void onChatroomSelected(View caller, int position) {
               Log.d(TAG, "OnClick: onChatroomSelected");
               ChatRoom chatroom = ChatRoomsIndexRecyclerViewAdapter.this.getItem(position);
               Intent intent = new Intent(parent.getContext(), ChatActivity.class);
               intent.putExtra(ChatActivity.CHAT_UID, chatroom.uid);
               intent.putExtra(ChatActivity.CHAT_TARGET, ChatActivity.CHAT_TARGET_CHATROOM);
               parent.getContext().startActivity(intent);
           }
       });

        return vh;
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
    }


}