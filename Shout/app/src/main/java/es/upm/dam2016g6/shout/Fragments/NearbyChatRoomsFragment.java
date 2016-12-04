package es.upm.dam2016g6.shout.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.upm.dam2016g6.shout.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NearbyChatRoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearbyChatRoomsFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_nearby_chat_rooms, container, false);
    }

}
