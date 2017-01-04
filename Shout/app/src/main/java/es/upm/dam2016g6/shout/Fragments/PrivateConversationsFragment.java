package es.upm.dam2016g6.shout.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.model.PrivateConversation;
import es.upm.dam2016g6.shout.support.PCIndexRecyclerViewAdapter;
import es.upm.dam2016g6.shout.support.PrivateConversationViewHolder;
import es.upm.dam2016g6.shout.support.Utils;


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

         // Add toolbar to fragment
        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar_private_conversations);
        toolbar.setTitle("Private Conversations");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Store likes into a recycler view and configure it
        RecyclerView rv = (RecyclerView) this.mView.findViewById(R.id.pc_rv_myConversations);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        DatabaseReference keyRef = FirebaseDatabase
                .getInstance()
                .getReference("/users/" + Utils.getCurrentUserUid() + "/privateChats/");
        DatabaseReference dataRef = FirebaseDatabase
                .getInstance()
                .getReference("/privateConversations/");
        PCIndexRecyclerViewAdapter adapter = new PCIndexRecyclerViewAdapter(
                PrivateConversation.class,
                R.layout.list_pc_item_layout,
                PrivateConversationViewHolder.class,
                keyRef,
                dataRef);
        rv.setAdapter(adapter);

        return mView;
    }

}
