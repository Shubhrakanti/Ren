package com.ren;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
        mPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            @Override
            public void onPageSelected(int position) {
                if( position == TabFragment.SAVED_TAB_INT ) {
                    BackgroundConn backgroundConn = new BackgroundConn( getActivity() );
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    backgroundConn.execute( BackgroundConn.OBTAIN_SAVED_USERS, sp.getString( "Login uname", null ) );
                }
            }
        });

        if( mPager != null && mPager.getAdapter() == null )
            mPager.setAdapter(mPagerAdapter);

        mTabs.set((SlidingTabLayout) view.findViewById(R.id.tabs));
        mTabs.get().setDistributeEvenly(true);
        mTabs.get().setViewPager(mPager);
        mTabs.get().setBackgroundColor(getResources().getColor(R.color.primaryColor));

    }
}
