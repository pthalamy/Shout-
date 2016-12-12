package es.upm.dam2016g6.shout.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.upm.dam2016g6.shout.R;
import es.upm.dam2016g6.shout.support.ChatRoomsFragmentPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomsFragment extends android.support.v4.app.Fragment {

    private FragmentTabHost mTabHost;

    public ChatRoomsFragment() {
        // Required empty public constructor
    }

    public static ChatRoomsFragment newInstance(String param1, String param2) {
        ChatRoomsFragment fragment = new ChatRoomsFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_chat_rooms, container, false);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) mView.findViewById(R.id.viewpager_chat_rooms);
        viewPager.setAdapter(new ChatRoomsFragmentPagerAdapter(getChildFragmentManager(), this.getActivity()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.sliding_tabs_chat_rooms);
        for (int i = 0; i < viewPager.getAdapter().getCount(); i++)
            tabLayout.addTab(tabLayout.newTab().setText(viewPager.getAdapter().getPageTitle(i)));
        tabLayout.setupWithViewPager(viewPager);

        // Add toolbar to fragment
        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar_chat_rooms);
        toolbar.setTitle("Chat Rooms");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return mView;
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_chat_rooms, container, false);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTabHost = null;
    }
}
