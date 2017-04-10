package com.ren;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

public class FullSizeImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_size_image);

        ImageView imageView = (ImageView) findViewById(R.id.full_size_image);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String toDecode = prefs.getString("Photo", Card.encodeTobase64(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.usericon)));
        byte[] decodedByte =  Base64.decode(toDecode, 0);
        Bitmap b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        imageView.setImageBitmap(b);
    }
}
