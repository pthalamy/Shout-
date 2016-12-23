package es.upm.dam2016g6.shout.support;

import android.util.Log;
import android.util.Pair;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import es.upm.dam2016g6.shout.model.ChatRoom;

/**
 * Created by pthalamy on 11/12/16.
 */

public class MyChatRoomsRecyclerViewAdapter extends FirebaseRecyclerAdapter<Pair, MyChatRoomViewHolder> {
    private static final String TAG = "TAG_" + MyChatRoomsRecyclerViewAdapter.class.getSimpleName();
    // Stores a reference to viewHolders that are waiting for Firebase database to be populated
    private Map<String, MyChatRoomViewHolder> viewHoldersAwaitingPopulation;

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
*                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public MyChatRoomsRecyclerViewAdapter(Class<Pair> modelClass, int modelLayout, Class<MyChatRoomViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        viewHoldersAwaitingPopulation = new HashMap<>();
    }

    @Override
    protected void populateViewHolder(MyChatRoomViewHolder viewHolder, Pair pair, int position) {
        Pair<String, Boolean> key = pair;
        this.viewHoldersAwaitingPopulation.put(key.first, viewHolder);
        DatabaseReference ref = Utils.getDatabase().getReference("/chatrooms/" + key.first);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoom chatroom = dataSnapshot.getValue(ChatRoom.class);
                MyChatRoomViewHolder viewHolder = MyChatRoomsRecyclerViewAdapter.
                        this.viewHoldersAwaitingPopulation.remove(chatroom.uid);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Cannot get one of the user's chatrooms");
            }
        });
    }
}