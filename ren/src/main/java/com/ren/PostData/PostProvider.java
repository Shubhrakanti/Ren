package com.ren.PostData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ren.PostData.PicturePostContract.PicturePostEntry;

/**
 * Created by giddu on 4/16/17.
 */

public class PostProvider extends ContentProvider {

    private PicturePostDbHelper picturePostDbHelper;
    private static final int POST = 100;
    private static final int POST_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PicturePostContract.CONTENT_AUTHORITY, PicturePostContract.PATH_ITEMS, POST);
        sUriMatcher.addURI(PicturePostContract.CONTENT_AUTHORITY, PicturePostContract.PATH_ITEMS + "/#", POST_ID);
    }

    @Override
    public boolean onCreate() {
        picturePostDbHelper = new PicturePostDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = picturePostDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case POST:
                cursor = database.query(PicturePostEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case POST_ID:
                selection = PicturePostEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(PicturePostEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getApplicationContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POST:
                return PicturePostEntry.CONTENT_LIST_TYPE;
            case POST_ID:
                return PicturePostEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POST:
                return insertPost(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPost(Uri uri, ContentValues values) {
        SQLiteDatabase db = picturePostDbHelper.getWritableDatabase();
        long id = db.insert(PicturePostEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
           }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
