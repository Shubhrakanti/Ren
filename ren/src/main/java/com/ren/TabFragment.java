package com.ren;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragment extends Fragment implements CardAdapter.ClickListener {
    // Use this to determine which row layout to inflate
    public static final int RECEIVED_TAB_INT = 0,
                            SAVED_TAB_INT = 1,
                            HOME_TAB_INT = 2,
                            MY_CARD_TAB_INT = 3;
//                            IGNORED_TAB_INT = 2;

    public static CardAdapter newReceivedCardAdapter;
    public static CardAdapter savedCardAdapter;
    //public static CardAdapter ignoredCardAdapter;

    public static TabFragment getInstance(int position) {
        TabFragment tabFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
//        Log.d("TabFragment", "Position: " + position );
        tabFragment.setArguments(args);
        return tabFragment;
    }

    public View onCreateView
            (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        // inflate other ones
        View layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
        //View layout = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            switch (bundle.getInt("position")) {
                case RECEIVED_TAB_INT:
//                    Log.e("TabFragment", "Recreating received tab");
                    layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
                    RecyclerView newReceivedRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
                    newReceivedCardAdapter = new CardAdapter(getActivity(), RECEIVED_TAB_INT);
                    // The fragment is the object which implement the ClickListener
                    newReceivedCardAdapter.setClickListener(this);
                    newReceivedCardAdapter.setCardList(SyncService.getReceivedCards());
                    newReceivedRecyclerView.setAdapter(newReceivedCardAdapter);
                    newReceivedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                case SAVED_TAB_INT:
//                    Log.e("TabFragment", "Recreating saved tab"
                    BackgroundConn bckconn = new BackgroundConn( getActivity() );
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
                    bckconn.execute( BackgroundConn.OBTAIN_SAVED_USERS, sp.getString("Login uname", ""));

                    layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
                    RecyclerView savedRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
                    savedCardAdapter = new CardAdapter(getActivity(), SAVED_TAB_INT);
                    // The fragment is the object which implement the ClickListener
                    savedCardAdapter.setClickListener(this);
                    savedCardAdapter.setCardList(SyncService.getSavedCards());
                    savedRecyclerView.setAdapter(savedCardAdapter);
                    savedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                /*case IGNORED_TAB_INT:
                    layout = inflater.inflate( R.layout.recyclerview_layout, container, false );
                    RecyclerView ignoredRecyclerView = (RecyclerView) layout.findViewById( R.id.recycler_view );
                    ignoredCardAdapter = new CardAdapter( getActivity(), IGNORED_TAB_INT );
                    // The fragment is the object which implement the ClickListener
                    ignoredCardAdapter.setClickListener( this );
                    ignoredCardAdapter.setCardList( SyncService.getIgnoredCards() );
                    ignoredRecyclerView.setAdapter( ignoredCardAdapter );
                    ignoredRecyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
                    break;*/
                /*default:
                    layout = inflater.inflate(R.layout.fragment_nearby_tab, container, false);
                    break;*/
            }
        }
        return layout;
    }

    @Override
    public void itemClicked(View view, Card card) {
        Intent i = new Intent(new Intent(getActivity(), CardDetailActivity.class));
        i.putExtra("Card", card);
        startActivity(i);
    }

}
