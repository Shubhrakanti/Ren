package com.ren;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alvin on 6/30/2016.
 * Used to display Profile Card Details
 */
public class MyCardFragment extends Fragment {
    private Card myCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myCardView = inflater.inflate( R.layout.my_card_details, container, false );
        myCard = ((MainActivity)getActivity()).getMyCard( false );

        ImageView im = (ImageView) myCardView.findViewById(R.id.my_detail_photo);
        if (myCard.getmPhotoEncoded().equals("Default")) {
            im.setImageResource(R.drawable.usericon);
        } else {
            im.setImageBitmap(myCard.decodeBase64());
        }

        im = (ImageView) myCardView.findViewById(R.id.my_detail_gender);
        if (myCard.getmGender().equals("UNKNOWN")) {
            im.setImageResource(0);
        } else if (myCard.getmGender().equals("MALE")) {
            im.setImageResource(R.drawable.male);
        } else if (myCard.getmGender().equals("FEMALE")) {
            im.setImageResource(R.drawable.female);
            // Log.e("Gender", "Set Female Icon");
        }

        TextView tv = (TextView) myCardView.findViewById(R.id.my_detail_name);
        tv.setText(myCard.getmName());

        if (!myCard.getmPhone().equals("")) {
            tv = (TextView) myCardView.findViewById(R.id.my_detail_phone);
            tv.setText(myCard.getmPhone());
        }

        if (!myCard.getmEmail().equals("")) {
            tv = (TextView) myCardView.findViewById(R.id.my_detail_email);
            tv.setText(myCard.getmEmail());
        }

//        if (!myCard.getmWebsite().equals("")) {
//            tv = (TextView) myCardView.findViewById(R.id.my_detail_website);
//            tv.setText(myCard.getmWebsite());
//        }

        if (!myCard.getmOther().equals("")) {
            tv = (TextView) myCardView.findViewById(R.id.my_detail_aboutme_full);
            tv.setText(myCard.getmOther());
        }

        ImageButton ib = (ImageButton) myCardView.findViewById(R.id.my_detail_facebook);
        if (!myCard.getmFacebook().equals("")) {
            ib.setVisibility(View.VISIBLE);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String url = getFacebookPageUrl() + myCard.getmFacebook();

                    if( MainActivity.DEBUG ) { Log.e("MyCardFragment", "Fb ID: " + myCard.getmFacebook()); }

                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(facebookIntent);

                }
            });
        }

        if (!myCard.getmInstagram().equals("")) {
            ib = (ImageButton) myCardView.findViewById(R.id.my_detail_instagram);
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
        return myCardView;
    }

    private String getFacebookPageUrl()
    {
        final String FACEBOOK_BASE_URL = "https://www.facebook.com/";
        try {
            int fbVersionCode = getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

            if(fbVersionCode >= 3002850)
                return "fb://facewebmodal/f?href=" + FACEBOOK_BASE_URL;
            else
                return "fb://page/";
        }catch (PackageManager.NameNotFoundException e ){
            return FACEBOOK_BASE_URL;
        }
    }

}
