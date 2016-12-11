package es.upm.dam2016g6.shout.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.ChatRoom;
import es.upm.dam2016g6.shout.support.ChatRoomViewHolder;
import es.upm.dam2016g6.shout.support.ChatRoomsRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyChatRoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyChatRoomsFragment extends Fragment {

    private List<ChatRoom> chatRooms;
    private View mView;

    public NearbyChatRoomsFragment() {
        // Required empty public constructor
    }

    public static NearbyChatRoomsFragment newInstance() {
        NearbyChatRoomsFragment fragment = new NearbyChatRoomsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_nearby_chat_rooms, container, false);

        // Store likes into a recycler view and configure it
        RecyclerView rv = (RecyclerView) this.mView.findViewById(R.id.rv_nearby_chat_rooms);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chatrooms");
        ChatRoomsRecyclerViewAdapter adapter = new ChatRoomsRecyclerViewAdapter(
                ChatRoom.class,
                R.layout.chatroom_item_layout,
                ChatRoomViewHolder.class,
                ref);
        rv.setAdapter(adapter);

        return mView;
    }

}
