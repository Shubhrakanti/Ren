package com.namecardsnearby;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                    Log.e("TabFragment", "Recreating saved tab");
                    layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
                    RecyclerView savedRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
                    savedCardAdapter = new CardAdapter(getActivity(), SAVED_TAB_INT);
                    // The fragment is the object which implement the ClickListener
                    savedCardAdapter.setClickListener(this);
                    savedCardAdapter.setCardList(SyncService.getSavedCards());
                    savedRecyclerView.setAdapter(savedCardAdapter);
                    savedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                case HOME_TAB_INT:
                    break;
                case MY_CARD_TAB_INT:
                    Log.e("TabFragment", "Recreating MYCARDTAB");
                    layout = inflater.inflate( R.layout.my_card_details, container, false );

                    final Card myCard = ((MainActivity)getActivity()).getMyCard();


                    ImageView im = (ImageView) layout.findViewById(R.id.my_detail_photo);
                    if (myCard.getmPhotoEncoded().equals("Default")) {
                        im.setImageResource(R.drawable.usericon);
                    } else {
                        im.setImageBitmap(myCard.decodeBase64());
                    }

                    im = (ImageView) layout.findViewById(R.id.my_detail_gender);
                    if (myCard.getmGender().equals("UNKNOWN")) {
                        im.setImageResource(0);
                    } else if (myCard.getmGender().equals("MALE")) {
                        im.setImageResource(R.drawable.male);
                    } else if (myCard.getmGender().equals("FEMALE")) {
                        im.setImageResource(R.drawable.female);
                        // Log.e("Gender", "Set Female Icon");
                    }

                    TextView tv = (TextView) layout.findViewById(R.id.my_detail_name);
                    tv.setText(myCard.getmName());

                    if (!myCard.getmPhone().equals("")) {
                        tv = (TextView) layout.findViewById(R.id.my_detail_phone);
                        tv.setText(myCard.getmPhone());
                    }

                    if (!myCard.getmEmail().equals("")) {
                        tv = (TextView) layout.findViewById(R.id.my_detail_email);
                        tv.setText(myCard.getmEmail());
                    }

                    if (!myCard.getmWebsite().equals("")) {
                        tv = (TextView) layout.findViewById(R.id.my_detail_website);
                        tv.setText(myCard.getmWebsite());
                    }

                    if (!myCard.getmOther().equals("")) {
                        tv = (TextView) layout.findViewById(R.id.my_detail_aboutme_full);
                        tv.setText(myCard.getmOther());
                    }

                    ImageButton ib = (ImageButton) layout.findViewById(R.id.my_detail_facebook);
                    if (!myCard.getmFacebook().equals("")) {
                        ib.setVisibility(View.VISIBLE);
                        ib.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String url = "fb://page/" + myCard.getmFacebook();
                                try {
                                    Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    startActivity(facebookAppIntent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.facebook_not_found), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                    if (!myCard.getmInstagram().equals("")) {
                        ib = (ImageButton) layout.findViewById(R.id.my_detail_instagram);
                        ib.setVisibility(View.VISIBLE);
                        ib.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String instagramUri = "http://instagram.com/_u/" + myCard.getmInstagram();
                                Uri uri = Uri.parse(instagramUri);
                                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                                likeIng.setPackage("com.instagram.android");

                                try {
                                    startActivity(likeIng);
                                } catch (ActivityNotFoundException e) {
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://instagram.com/" + myCard.getmInstagram())));
                                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.instagram_not_found), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

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
