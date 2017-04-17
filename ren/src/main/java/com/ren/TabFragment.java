package com.ren;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;
import com.kylewbanks.android.iconedittext.IconEditText;
import com.theartofdev.edmodo.cropper.CropImage;

public class TabFragment extends Fragment implements CardAdapter.ClickListener {
    // Use this to determine which row layout to inflate
    public static final int RECEIVED_TAB_INT = 0,
                            SAVED_TAB_INT = 1,
                       HOME_TAB_INT = 3,
                            MY_CARD_TAB_INT = 2;
//                            IGNORED_TAB_INT = 2;
    private static final int PHOTO_SELECTED = 101;
    private static final int PICK_CROP = 100;
    public static boolean DEBUG = false;
    private final String TAG = "Connect Fragment";

    public static CardAdapter newReceivedCardAdapter;
    public static CardAdapter savedCardAdapter;
    //public static CardAdapter ignoredCardAdapter;

    private String userPhotoStr = "Default";

    private Card myCard;
    private View rootView;

    public static TabFragment getInstance(int position) {
        TabFragment tabFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
//        Log.d("TabFragment", "Position: " + position );
        tabFragment.setArguments(args);
        return tabFragment;
    }

    public View onCreateView
            (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        // inflate other ones
        View layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
        //View layout = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            switch (bundle.getInt("position")) {
                case RECEIVED_TAB_INT:
//                    Log.e("TabFragment", "Recreating received tab");
                    layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
                    RecyclerView newReceivedRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
                    newReceivedCardAdapter = new CardAdapter(getActivity(), RECEIVED_TAB_INT);
                    // The fragment is the object which implement the ClickListener
                    newReceivedCardAdapter.setClickListener(this);
                    newReceivedCardAdapter.setCardList(SyncService.getReceivedCards());
                    newReceivedRecyclerView.setAdapter(newReceivedCardAdapter);
                    newReceivedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                case SAVED_TAB_INT:
//                    Log.e("TabFragment", "Recreating saved tab"
                    BackgroundConn bckconn = new BackgroundConn( getActivity() );
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
                    bckconn.execute( BackgroundConn.OBTAIN_SAVED_USERS, sp.getString("Login uname", ""));

                    layout = inflater.inflate(R.layout.recyclerview_layout, container, false);
                    RecyclerView savedRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
                    savedCardAdapter = new CardAdapter(getActivity(), SAVED_TAB_INT);
                    // The fragment is the object which implement the ClickListener
                    savedCardAdapter.setClickListener(this);
                    savedCardAdapter.setCardList(SyncService.getSavedCards());
                    savedRecyclerView.setAdapter(savedCardAdapter);
                    savedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    break;
                /*case IGNORED_TAB_INT:
                    layout = inflater.inflate( R.layout.recyclerview_layout, container, false );
                    RecyclerView ignoredRecyclerView = (RecyclerView) layout.findViewById( R.id.recycler_view );
                    ignoredCardAdapter = new CardAdapter( getActivity(), IGNORED_TAB_INT );
                    // The fragment is the object which implement the ClickListener
                    ignoredCardAdapter.setClickListener( this );
                    ignoredCardAdapter.setCardList( SyncService.getIgnoredCards() );
                    ignoredRecyclerView.setAdapter( ignoredCardAdapter );
                    ignoredRecyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
                    break;*/
                /*default:
                    layout = inflater.inflate(R.layout.fragment_nearby_tab, container, false);
                    break;*/
                case MY_CARD_TAB_INT:
                    layout = inflater.inflate(R.layout.fragment_converse_profile, container, false);
                    rootView = layout;
                    setUpMyCard(layout);
                    setUpEditMyCard(layout);



            }
        }
        return layout;
    }

    @Override
    public void itemClicked(View view, Card card) {
        Intent i = new Intent(new Intent(getActivity(), CardDetailActivity.class));
        i.putExtra("Card", card);
        startActivity(i);
    }

    private void setUpMyCard (final View myCardView){
        myCard = ((MainActivity)getActivity()).getMyCard( false );

        ImageView im = (ImageView) myCardView.findViewById(R.id.my_detail_photo_perm);
        if (myCard.getmPhotoEncoded().equals("Default")) {
            im.setImageResource(R.drawable.usericon);
        } else {
            im.setImageBitmap(myCard.decodeBase64());
        }


        TextView tv = (TextView) myCardView.findViewById(R.id.my_detail_name_perm);
        tv.setText(myCard.getmName());

        if (!myCard.getmPhone().equals("")) {
            tv = (TextView) myCardView.findViewById(R.id.my_detail_phone_perm);
            tv.setText(myCard.getmPhone());
        }

        if (!myCard.getmEmail().equals("")) {
            tv = (TextView) myCardView.findViewById(R.id.my_detail_email_perm);
            tv.setText(myCard.getmEmail());
        }


        if (!myCard.getmOther().equals("")) {
            tv = (TextView) myCardView.findViewById(R.id.my_detail_aboutme_perm);
            tv.setText(myCard.getmOther());
        }

        ImageButton ib = (ImageButton) myCardView.findViewById(R.id.my_detail_facebook_perm);
        if (!myCard.getmFacebook().equals("")) {
            ib.setVisibility(View.VISIBLE);
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String url = getFacebookPageUrl() + myCard.getmFacebook();

                    if( MainActivity.DEBUG ) { Log.e("ContributeFragment", "Fb ID: " + myCard.getmFacebook()); }

                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(facebookIntent);

                }
            });
        }

        if (!myCard.getmInstagram().equals("")) {
            ib = (ImageButton) myCardView.findViewById(R.id.my_detail_instagram_perm);
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


        Button editMyCard = (Button) myCardView.findViewById(R.id.edit_my_card_perm);
        editMyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCardView.findViewById(R.id.edit_layout).setVisibility(View.VISIBLE);
                myCardView.findViewById(R.id.perm_layout).setVisibility(View.GONE);
            }
        });
    }

    private void setUpEditMyCard(final View layout){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        EditText editText = (EditText) layout.findViewById(R.id.user_name);
        String name = prefs.getString("Name", null);
        editText.setText(name);

        IconEditText iet = (IconEditText) layout.findViewById(R.id.user_phone);
        editText = iet.getEditText();
        String phone = prefs.getString("Phone", null);
        editText.setText(phone);

        iet = (IconEditText) layout.findViewById(R.id.email_address);
        editText = iet.getEditText();
        String email = prefs.getString("Email", null);
        editText.setText(email);

        String fbId = prefs.getString("Facebook", "");
        if( fbId.equals("") ) {
            LoginButton fbButton = (LoginButton) layout.findViewById( R.id.facebook_login_button );
            Button fbRemoveProfile = (Button) layout.findViewById(R.id.fb_remove_profile_button);

            fbButton.setVisibility(View.VISIBLE);
            fbRemoveProfile.setVisibility(View.GONE);
        }
        else {
            LoginButton fbButton = (LoginButton) layout.findViewById( R.id.facebook_login_button );
            Button      fbRemoveProfile = (Button) layout.findViewById(R.id.fb_remove_profile_button);

            fbButton.setVisibility(View.GONE);
            fbRemoveProfile.setVisibility(View.VISIBLE);
        }

        iet = (IconEditText) layout.findViewById(R.id.instagram);
        if (iet != null) {
            editText = iet.getEditText();
            String ig = prefs.getString("Instagram", null);
            editText.setText(ig);
        }
        editText = (EditText) layout.findViewById(R.id.about_me);
        String aboutMe = prefs.getString("AboutMe", null);
        editText.setText(aboutMe);

        ImageButton ib = (ImageButton) layout.findViewById(R.id.user_photo_button);
        String userPhotoStr = prefs.getString("Photo", "Default");
        Log.d(TAG,"User photo from shared prefs: " + userPhotoStr);
        if (!userPhotoStr.equals("Default")) {
            byte[] decodedByte = Base64.decode(userPhotoStr, 0);
            Bitmap b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            ib.setImageBitmap(b);
        } else {
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.usericon);
            ib.setImageBitmap(b);
        }
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("MainActivity", "User photo clicked" );
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    selectImage();
//                else
//                    doCrop();
            }
        });

        Button updateBtn = (Button) layout.findViewById(R.id.update_profile);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send data to database
                updateProfile(layout);
                saveEditData(layout);
                layout.findViewById(R.id.edit_layout).setVisibility(View.GONE);
                layout.findViewById(R.id.perm_layout).setVisibility(View.VISIBLE);
                setUpMyCard(layout);
                Toast.makeText( getContext(), "Profile updated..", Toast.LENGTH_SHORT).show();
            }
        });

        Button cancelBtn = (Button) layout.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.findViewById(R.id.edit_layout).setVisibility(View.GONE);
                layout.findViewById(R.id.perm_layout).setVisibility(View.VISIBLE);
            }
        });


    }

    public void updateProfile(View view)
    {
        BackgroundConn bckConn = new BackgroundConn(getContext());
        // Register process needs modification
        Card myCard = getMyCard( view );
        //String uName = BackgroundConn.USERNAME;
        Log.e("Edit Card From Drawer", "Photo from card:" + myCard.getmPhotoEncoded());
        bckConn.execute("update_profile", myCard.getmName(), myCard.getmPhone(), myCard.getmEmail(), myCard.getmGender(),
                myCard.getmFacebook(), myCard.getmInstagram(),
//                myCard.getmWebsite(), myCard.getmOther(), myCard.getmPhotoEncoded(), myCard.getUname());
                myCard.getmOther(), myCard.getmPhotoEncoded(), myCard.getUname());

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

    public Card getMyCard(View view) {

        EditText editText = (EditText) view.findViewById(R.id.user_name);
        String name = editText.getText().toString();

        IconEditText iet = (IconEditText) view.findViewById(R.id.user_phone);
        String phone = iet.getText().toString();

        iet = (IconEditText) view.findViewById(R.id.email_address);
        String email = iet.getText().toString();

        Profile fbProfile = Profile.getCurrentProfile();
        String fb = "";
        if( fbProfile != null )
            fb = fbProfile.getId();

        iet = (IconEditText) view.findViewById(R.id.instagram);
        String ig = "";
        if (iet != null) {
            ig = iet.getText().toString();
        }

//        iet = (IconEditText) findViewById(R.id.website);
//        String website = iet.getText().toString();

        editText = (EditText) view.findViewById(R.id.about_me);
        String aboutMe = editText.getText().toString();

        if (!userPhotoStr.equals("Default")) {
            // User has custom photo
            ImageButton ib = (ImageButton) view.findViewById(R.id.user_photo_button);
            Bitmap customPhoto = ((BitmapDrawable) ib.getDrawable()).getBitmap();
            Log.d("MainActivity", (customPhoto == null ? "true" : "false") );
            userPhotoStr = Card.encodeTobase64(customPhoto);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String uName = prefs.getString("Login_uname", "");
        /*Log.e("GetMyCard", "Ready to update server.");
        Log.e("GetMyCard", "uName is: "+uName);*/
        return new Card(uName, name, "Male", userPhotoStr,
//                        phone, email, fb, ig, website, aboutMe);
                phone, email, fb, ig, aboutMe);
    }

    private void saveEditData (View view){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();

        EditText editText = (EditText) view.findViewById(R.id.user_name);
        editor.putString("Name", editText.getText().toString());

        editor.putString("Photo", userPhotoStr);

        IconEditText iet = (IconEditText) view.findViewById(R.id.user_phone);
        editor.putString("Phone", iet.getEditText().getText().toString());

        iet = (IconEditText) view.findViewById(R.id.email_address);
        editor.putString("Email", iet.getEditText().getText().toString());

        Profile fbProfile = Profile.getCurrentProfile();
        if( fbProfile != null )
            editor.putString("Facebook", fbProfile.getId());
        else
            editor.putString("Facebook", "");

        iet = (IconEditText) view.findViewById(R.id.instagram);
        if (iet != null)
            editor.putString("Instagram", iet.getEditText().getText().toString());

//        iet = (IconEditText) findViewById(R.id.website);
//        editor.putString("Website", iet.getEditText().getText().toString());

        editText = (EditText) view.findViewById(R.id.about_me);
        editor.putString("AboutMe", editText.getText().toString());


        editor.apply();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_SELECTED:
                if (data != null) {
                    Uri image = data.getData();
                    final ContentResolver contentResolver = getContext().getContentResolver();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, image);
                        ImageButton ib = (ImageButton) rootView.findViewById(R.id.user_photo_button);
                        ib.setImageBitmap(bitmap);
                        if (bitmap != null) {
                            userPhotoStr = Card.encodeTobase64(bitmap);
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            // User photo is saved as String
                            editor.putString("Photo", userPhotoStr);
                            editor.apply();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
//            case PICK_CROP:
//                if (data != null) {
//                    Bundle extras = data.getExtras();
//                    if (extras == null) {
//                        //Log.d("MainActivity", "ActivityResult::PICK_CROP");
//                        break;
//                    }
//                    Bitmap selectedBitmap = extras.getParcelable("data");
//                    ImageButton ib = (ImageButton) rootView.findViewById(R.id.user_photo_button);
//                    ib.setImageBitmap(selectedBitmap);
//
//                    // Save the custom photo, otherwise onResume will cancel changes
//                    if (selectedBitmap != null) {
//                        userPhotoStr = Card.encodeTobase64(selectedBitmap);
//                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//                        SharedPreferences.Editor editor = prefs.edit();
//                        // User photo is saved as String
//                        editor.putString("Photo", userPhotoStr);
//                        editor.apply();
//                    }
//                }
//                break;
//            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                if(resultCode == getActivity().RESULT_OK) {
//                    Uri resultUri = result.getUri();
//
//                    Bitmap resizedImage = BitmapFactory.decodeFile(resultUri.getPath());
//
//                    ImageButton ib = (ImageButton) rootView.findViewById(R.id.user_photo_button);
//                    ib.setImageBitmap(resizedImage);
//
//                    if(DEBUG) { Log.e(TAG, "Cropped Image size: " + (resizedImage.getByteCount()/1000) + "kb"); }
//
//                    // Save the custom photo, otherwise onResume will cancel changes
//                    if (resizedImage!= null) {
//                        userPhotoStr = Card.encodeTobase64(resizedImage);
//                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//                        SharedPreferences.Editor editor = prefs.edit();
//                        // User photo is saved as String
//                        editor.putString("Photo", userPhotoStr);
//                        editor.apply();
//                    }
//
//                } else if( resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Toast.makeText(getActivity(), "Failed image cropping.",Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectImage()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_SELECTED);

    }

    private void doCrop() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("return-data", true);
        photoPickerIntent.putExtra("aspectX", 1);
        photoPickerIntent.putExtra("aspectY", 1);
        photoPickerIntent.putExtra("outputX", 200);
        photoPickerIntent.putExtra("outputY", 200);
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        //Log.d("MainActivity:doCrop", "Starting cropper" );
        startActivityForResult(photoPickerIntent, PICK_CROP);
    }
}
