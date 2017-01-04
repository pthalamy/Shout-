package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.Chat;

/**
 * Created by pthalamy on 3/1/17.
 */

public class ChatRecyclerViewAdapter extends FirebaseRecyclerAdapter<Chat, ChatHolder> {
    private static final int MY_MESSAGE = 0x0;
    private static final int OTHER_MESSAGE = 0x1;

    private final Context mContext;

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public ChatRecyclerViewAdapter(Class<Chat> modelClass, int modelLayout, Class<ChatHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MY_MESSAGE) {
            return new ChatHolder(LayoutInflater.from(this.mContext).inflate(R.layout.my_chat_item_layout, parent, false));
        } else {
            return new ChatHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_layout, parent, false));
        }
    }


    @Override
    public int getItemViewType(int position) {
        Chat item = this.getItem(position);

        if (item.isMine()) return MY_MESSAGE;
        else return OTHER_MESSAGE;
    }

    @Override
    protected void populateViewHolder(ChatHolder viewHolder, Chat chat, int position) {
        viewHolder.setText(chat.text);
        viewHolder.setName(chat.name);
        viewHolder.setDate(chat.timestamp);
        viewHolder.setUserPic(chat.userPicUrl, this.mContext);
    }
}
