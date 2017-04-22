package com.ren;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ren.PostData.PicturePostContract.PicturePostEntry;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.facebook.login.widget.ProfilePictureView.TAG;


/**
 * Created by Alvin on 6/30/2016.
 * Used to display Profile Card Details
 */
public class ContributeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView recList;
    private Button newPost;
    private static final int REQUEST_IMAGE_CAPTURE = 105;
    private static final int LOADER_ID = 0;
    private Uri mImageUri;
    private PicturePostCursorAdapter picturePostCursorAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate( R.layout.fragment_contribute, container, false );

        recList = (RecyclerView) layout.findViewById(R.id.cardList);
        recList.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        picturePostCursorAdapter = new PicturePostCursorAdapter(getContext(),null);
        recList.setAdapter(picturePostCursorAdapter);


        newPost = (Button) layout.findViewById(R.id.newPost);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBackCamera();
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return layout;
    }

    public void openBackCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo;
        try
        {
            photo = this.createTemporaryFile("picture", ".jpg");
            photo.delete();
        }
        catch(Exception e)
        {
            Log.v(TAG, "Can't create file to take picture!");
            Toast.makeText(getActivity(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT);
            return;
        }
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"Starting Result processing");
        getActivity().getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = getActivity().getContentResolver();
        final Bitmap bitmap;
        try
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Enter Caption");
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContentValues values = new ContentValues();
                    values.put(PicturePostEntry.COLUMN_POST_USERNAME, "Sample Username");
                    values.put(PicturePostEntry.COLUMN_POST_CAPTION, input.getText().toString());
                    values.put(PicturePostEntry.COLUMN_POST_COMMENTS, "15");
                    values.put(PicturePostEntry.COLUMN_POST_LIKES, "20");
                    values.put(PicturePostEntry.COLUMN_POST_GPS, "0,0");
                    values.put(PicturePostEntry.COLUMN_POST_TIME, "sample time");
                    values.put(PicturePostEntry.COLUMN_POST_IMAGE, mImageUri.toString());

                    BackgroundConn bckConn = new BackgroundConn(getContext());
                    String bitmapString = Card.encodeTobase64(bitmap);
                    bckConn.execute("shu", "sample username", "0,0", input.getText().toString(), "sample time", bitmapString);

                    getActivity().getContentResolver().insert(PicturePostEntry.CONTENT_URI, values);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PicturePostEntry._ID,
                PicturePostEntry.COLUMN_POST_USERNAME,
                PicturePostEntry.COLUMN_POST_CAPTION,
                PicturePostEntry.COLUMN_POST_LIKES,
                PicturePostEntry.COLUMN_POST_COMMENTS,
                PicturePostEntry.COLUMN_POST_TIME,
                PicturePostEntry.COLUMN_POST_GPS,
                PicturePostEntry.COLUMN_POST_IMAGE
        };

        return  new CursorLoader(getActivity(),
                PicturePostEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null){
            picturePostCursorAdapter.swapCursor(cursor);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        picturePostCursorAdapter.swapCursor(null);
    }
}
