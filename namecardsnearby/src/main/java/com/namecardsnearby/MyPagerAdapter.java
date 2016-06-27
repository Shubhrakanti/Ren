package com.namecardsnearby;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

// Insert child to ViewPager
public class MyPagerAdapter extends FragmentPagerAdapter {
    private final String tabText[];

    public MyPagerAdapter(android.support.v4.app.FragmentManager fm, Context c) {
        super(fm);
        tabText = c.getResources().getStringArray(R.array.tabs);
    }

    @Override
    public Fragment getItem(int position) {
        // Log.e("PagerAdapter", position + "");
        // Only called once
        return TabFragment.getInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabText[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
