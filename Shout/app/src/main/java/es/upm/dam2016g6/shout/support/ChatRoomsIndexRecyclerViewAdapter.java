package es.upm.dam2016g6.shout.support;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.Query;

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
    protected void populateViewHolder(ListChatRoomViewHolder viewHolder, ChatRoom chatRoom, int position) {
        // TODO: 23/12/16 implement
    }
}