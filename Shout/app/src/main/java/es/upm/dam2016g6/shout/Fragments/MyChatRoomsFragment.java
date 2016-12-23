package es.upm.dam2016g6.shout.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.support.MyChatRoomViewHolder;
import es.upm.dam2016g6.shout.support.MyChatRoomsRecyclerViewAdapter;
import es.upm.dam2016g6.shout.support.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyChatRoomsFragment extends Fragment {

    private View mView;

    public MyChatRoomsFragment() {
        // Required empty public constructor
    }

    public static MyChatRoomsFragment newInstance() {
        MyChatRoomsFragment fragment = new MyChatRoomsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                                          Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_my_chat_rooms, container, false);

        // Store likes into a recycler view and configure it
        RecyclerView rv = (RecyclerView) this.mView.findViewById(R.id.rv_nearby_chat_rooms);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("users/" + Utils.getCurrentUserUid() + "/userChatroomsUids/");
        MyChatRoomsRecyclerViewAdapter adapter = new MyChatRoomsRecyclerViewAdapter(
                Pair.class,
                R.layout.chatroom_item_layout,
                MyChatRoomViewHolder.class,
                ref);
        rv.setAdapter(adapter);

        return mView;

    }



}
