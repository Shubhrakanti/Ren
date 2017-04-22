package com.ren;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ren.PostData.PicturePostContract.PicturePostEntry;

import java.util.ArrayList;

/**
 * Created by giddu on 3/2/17.
 */

public class PicturePostCursorAdapter extends CursorRecyclerViewAdapter<PicturePostViewHolder> {

    Context context;

    public PicturePostCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }


    @Override
    public void onBindViewHolder(PicturePostViewHolder viewHolder, Cursor cursor) {

        Log.d("Cursor Value", String.valueOf(cursor));

        int username_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_USERNAME);
        int caption_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_CAPTION);
        int comments_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_COMMENTS);
        int likes_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_LIKES);
        int img_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_IMAGE);

        viewHolder.username.setText(cursor.getString(username_id));
        viewHolder.caption.setText(cursor.getString(caption_id));
        viewHolder.numComments.setText(String.valueOf(cursor.getInt(comments_id)));
        viewHolder.numLikes.setText(String.valueOf(cursor.getInt(likes_id)));

        String postBitmapURIString = cursor.getString(img_id);
        Uri postBitmapURI = Uri.parse(postBitmapURIString);
        viewHolder.mainPost.setImageURI(postBitmapURI);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userPhotoStr = prefs.getString("Photo", "Default");
        if (!userPhotoStr.equals("Default")) {
            byte[] decodedByte = Base64.decode(userPhotoStr, 0);
            Bitmap b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            viewHolder.profilePicture.setImageBitmap(b);
        } else {
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.usericon);
            viewHolder.profilePicture.setImageBitmap(b);
        }

    }

    @Override
    public PicturePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item_view, parent, false);
        return new PicturePostViewHolder(itemView);
    }
}