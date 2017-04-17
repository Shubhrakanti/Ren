package com.ren;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by giddu on 3/2/17.
 */

public class PicturePostViewHolder extends RecyclerView.ViewHolder {

    protected ImageView mainPost;
    protected CircleImageView profilePicture;
    protected TextView numLikes;
    protected TextView numComments;
    protected TextView username;
    protected TextView timePosted;
    protected TextView caption;

    public PicturePostViewHolder(View v){
        super (v);
        mainPost = (ImageView) v.findViewById(R.id.main_image_post);
        profilePicture = (CircleImageView) v.findViewById(R.id.profile_pic_img);
        numLikes = (TextView) v.findViewById(R.id.num_likes_text_view);
        numComments = (TextView) v.findViewById(R.id.num_comments_text_view);
        username = (TextView) v.findViewById(R.id.user_name);
        timePosted = (TextView) v.findViewById(R.id.time_till_post);
        caption = (TextView) v.findViewById(R.id.caption_text_view);

    }

}
