package com.namecardsnearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragment extends Fragment implements CardAdapter.ClickListener {
    public static CardAdapter newReceivedCardAdapter;
    public static CardAdapter savedCardAdapter;

    public static TabFragment getInstance(int position) {
        TabFragment tabFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
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
                case 0:
                    layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
                    RecyclerView newReceivedRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
                    newReceivedCardAdapter = new CardAdapter(getActivity());
                    // The fragment is the object which implement the ClickListener
                    newReceivedCardAdapter.setClickListener(this);
                    newReceivedCardAdapter.setCardList(SyncService.getReceivedCards());
                    newReceivedRecyclerView.setAdapter(newReceivedCardAdapter);
                    newReceivedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                case 1:
                    layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
                    RecyclerView savedRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
                    savedCardAdapter = new CardAdapter(getActivity());
                    // The fragment is the object which implement the ClickListener
                    savedCardAdapter.setClickListener(this);
                    savedCardAdapter.setCardList(SyncService.getSavedCards());
                    savedRecyclerView.setAdapter(savedCardAdapter);
                    savedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
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
