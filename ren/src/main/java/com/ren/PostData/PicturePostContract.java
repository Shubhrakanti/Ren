package com.ren.PostData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by giddu on 4/16/17.
 */

public class PicturePostContract {

    private PicturePostContract(){}

    public static final String CONTENT_AUTHORITY = "com.ren";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "posts";

    public static final class PicturePostEntry implements BaseColumns {

        /** The content URI to access the item data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public final static String TABLE_NAME = "posts";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_POST_USERNAME = "username";
        public final static String COLUMN_POST_CAPTION = "caption";
        public final static String COLUMN_POST_LIKES = "likes";
        public final static String COLUMN_POST_COMMENTS = "comments";
        public final static String COLUMN_POST_GPS = "gps";
        public final static String COLUMN_POST_TIME = "time";
        public final static String COLUMN_POST_IMAGE = "img";
        public final static String COLUMN_POST_PROF_PIC = "profpic";

    }
}
