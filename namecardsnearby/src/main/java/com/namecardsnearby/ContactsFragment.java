package com.namecardsnearby;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Alvin on 6/30/2016.
 * ContactFragment used to hold display Swipeable TabFragment
 */
public class ContactsFragment extends Fragment {
    public static final AtomicReference<SlidingTabLayout> mTabs = new AtomicReference<>();
    private static ViewPager mPager = null;
    private static MyPagerAdapter mPagerAdapter = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contactView = inflater.inflate( R.layout.contacts_fragment_swipe_layout, container, false);

        return contactView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPager = (ViewPager) view.findViewById(R.id.pager); // Pager is the area where recyclerview shows

        if( mPager != null && mPager.getAdapter() == null )
            mPager.setAdapter(mPagerAdapter);

        mTabs.set((SlidingTabLayout) view.findViewById(R.id.tabs));
        mTabs.get().setDistributeEvenly(true);
        mTabs.get().setViewPager(mPager);
        mTabs.get().setBackgroundColor(getResources().getColor(R.color.primaryColor));

    }
}
