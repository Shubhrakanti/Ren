package com.ren;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CardDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        //getSupportActionBar().setHomeButtonEnabled(true); // Show the return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Return only one level

        final Card cardClicked = (Card) getIntent().getExtras().get("Card");

        setTitle(cardClicked.getmName());

        ImageView im = (ImageView) findViewById(R.id.detail_photo);
        if (cardClicked.getmPhotoEncoded().equals("Default")) {
            im.setImageResource(R.drawable.usericon);
        } else {
            im.setImageBitmap(cardClicked.decodeBase64());
        }

        im = (ImageView) findViewById(R.id.detail_gender);
        if (cardClicked.getmGender().equals("UNKNOWN")) {
            im.setImageResource(0);
        } else if (cardClicked.getmGender().equals("MALE")) {
            im.setImageResource(R.drawable.male);
        } else if (cardClicked.getmGender().equals("FEMALE")) {
            im.setImageResource(R.drawable.female);
            // Log.e("Gender", "Set Female Icon");
        }

        TextView tv = (TextView) findViewById(R.id.detail_name);
        tv.setText(cardClicked.getmName());

        if (!cardClicked.getmPhone().equals("")) {
            tv = (TextView) findViewById(R.id.detail_phone);
            tv.setText(cardClicked.getmPhone());
        }

        if (!cardClicked.getmEmail().equals("")) {
            tv = (TextView) findViewById(R.id.detail_email);
            tv.setText(cardClicked.getmEmail());
        }

        if (!cardClicked.getmWebsite().equals("")) {
            tv = (TextView) findViewById(R.id.detail_website);
            tv.setText(cardClicked.getmWebsite());
        }

        if (!cardClicked.getmOther().equals("")) {
            tv = (TextView) findViewById(R.id.detail_aboutme_full);
            tv.setText(cardClicked.getmOther());
        }

        ImageButton ib = (ImageButton) findViewById(R.id.detail_facebook);
        if (!cardClicked.getmFacebook().equals("")) {
            ib.setVisibility(View.VISIBLE);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String url = "fb://page/" + cardClicked.getmFacebook();
                    try {
                        Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        startActivity(facebookAppIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.facebook_not_found), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        if (!cardClicked.getmInstagram().equals("")) {
            ib = (ImageButton) findViewById(R.id.detail_instagram);
            ib.setVisibility(View.VISIBLE);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String instagramUri = "http://instagram.com/_u/" + cardClicked.getmInstagram();
                    Uri uri = Uri.parse(instagramUri);
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://instagram.com/" + cardClicked.getmInstagram())));
                        Toast.makeText(getApplicationContext(), getString(R.string.instagram_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        /*if (!cardClicked.mWechat.equals("")) {
            ib = (ImageButton) findViewById(R.id.detail_wechat);
            ib.setVisibility(View.VISIBLE);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Wechat", cardClicked.mWechat);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), getString(R.string.wechat_copied), Toast.LENGTH_SHORT).show();
                }
            });
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
