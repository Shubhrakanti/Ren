package com.ren.PostData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ren.PostData.PicturePostContract.PicturePostEntry;

/**
 * Created by giddu on 4/16/17.
 */

public class PicturePostDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "posts.db";

    private static final int DATABASE_VERSION = 1;

    public PicturePostDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_POSTS_TABLE =  "CREATE TABLE " + PicturePostEntry.TABLE_NAME + " ("
                + PicturePostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PicturePostEntry.COLUMN_POST_USERNAME+ " TEXT, "
                + PicturePostEntry.COLUMN_POST_CAPTION + " TEXT, "
                + PicturePostEntry.COLUMN_POST_LIKES+ " INTEGER, "
                + PicturePostEntry.COLUMN_POST_COMMENTS+ " INTEGER, "
                + PicturePostEntry.COLUMN_POST_TIME+ " TEXT, "
                + PicturePostEntry.COLUMN_POST_GPS+ " TEXT,"
                + PicturePostEntry.COLUMN_POST_IMAGE + " TEXT);";

        db.execSQL(SQL_CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
