package es.upm.dam2016g6.shout.support;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import es.upm.dam2016g6.shout.fragments.InvitationsFragment;
import es.upm.dam2016g6.shout.fragments.MyChatRoomsFragment;
import es.upm.dam2016g6.shout.fragments.NearbyChatRoomsFragment;

/**
 * Created by pthalamy on 4/12/16.
 */

public class ChatRoomsFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Nearby", "My Rooms", "Invitations" };
    private Context context;

    public ChatRoomsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return NearbyChatRoomsFragment.newInstance();
            case 1: return MyChatRoomsFragment.newInstance();
            case 2: return InvitationsFragment.newInstance();
            default:
                throw new Error("Out of bound fragment position!");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}