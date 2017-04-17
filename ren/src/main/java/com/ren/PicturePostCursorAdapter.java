package com.ren;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    public PicturePostCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }


    @Override
    public void onBindViewHolder(PicturePostViewHolder viewHolder, Cursor cursor) {

        Log.d("Cursor Value", String.valueOf(cursor));

        int username_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_USERNAME);
        int caption_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_CAPTION);
        int comments_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_COMMENTS);
        int likes_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_LIKES);
        int profPic_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_PROF_PIC);
        int img_id = cursor.getColumnIndex(PicturePostEntry.COLUMN_POST_IMAGE);

        viewHolder.username.setText(cursor.getString(username_id));
        viewHolder.caption.setText(cursor.getString(caption_id));
        viewHolder.numComments.setText(String.valueOf(cursor.getInt(comments_id)));
        viewHolder.numLikes.setText(String.valueOf(cursor.getInt(likes_id)));

        String postBitmapString = cursor.getString(img_id);
        Bitmap postBitmap = null;
        try {
            byte [] encodeByte= Base64.decode(postBitmapString,Base64.DEFAULT);
            postBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
        }

        if(postBitmap != null){
            viewHolder.mainPost.setImageBitmap(postBitmap);
        }

        String profBitmapString = cursor.getString(profPic_id);
        Bitmap profBitmap = null;
        try {
            byte [] encodeByte= Base64.decode(profBitmapString,Base64.DEFAULT);
            postBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
        }

        if(postBitmap != null){
            viewHolder.profilePicture.setImageBitmap(profBitmap);
        }


    }

    @Override
    public PicturePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item_view, parent, false);
        return new PicturePostViewHolder(itemView);
    }
}