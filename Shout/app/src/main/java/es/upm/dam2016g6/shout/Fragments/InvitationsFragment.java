package es.upm.dam2016g6.shout.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.upm.dam2016g6.shout.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvitationsFragment extends Fragment {


    public InvitationsFragment() {
        // Required empty public constructor
    }

    public static InvitationsFragment newInstance() {
        InvitationsFragment fragment = new InvitationsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invitations, container, false);
    }

}
