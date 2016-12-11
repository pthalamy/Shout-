package es.upm.dam2016g6.shout.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.upm.dam2016g6.shout.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivateConversationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivateConversationsFragment extends android.support.v4.app.Fragment {

    private View mView;

    public PrivateConversationsFragment() {
        // Required empty public constructor
    }

    public static PrivateConversationsFragment newInstance() {
        PrivateConversationsFragment fragment = new PrivateConversationsFragment();
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
        mView = inflater.inflate(R.layout.fragment_private_conversations, container, false);

         // Add toolbar to fragment (contains logout button)
        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar_private_conversations);
        toolbar.setTitle("Private Conversations");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return mView;
    }

}
