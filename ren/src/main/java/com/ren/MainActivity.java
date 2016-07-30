package com.ren;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.kylewbanks.android.iconedittext.IconEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {
    // Debug purposes
    public static boolean DEBUG = false;
    private final String TAG = "MainActivity";
    // Crop
    private static final int PICK_CROP = 100;
    // Variables used for Profile Photo
    private static final int PHOTO_SELECTED = 101;
    private static final int PHOTO_WIDTH = 200, PHOTO_HEIGHT = 200;
    // For service
    private SyncService syncService;
    private boolean isBound = false;
    // Custom photo?
    private String userPhotoStr = "Default";

    // For fragment tabs
    private FragmentTabHost bottomFragmentTabHost;
    private final String HOME_TAB_TAG = "HOME_TAB_TAG";
    private final String CONTACT_TAB_TAG = "CONTACT_TAB_TAG";
    private final String MYCARD_TAB_TAG = "MYCARD_TAB_TAG";

    private final int   HOME_TAB_INDEX = 0,
                        CONTACT_TAB_INDEX = 1,
                        MY_CARD_TAB_INDEX = 2;

    // Gender
    private enum Gender {
        UNKNOWN, MALE, FEMALE
    }

    private Gender userGender = Gender.UNKNOWN;
    // Navigation
    private NavigationDrawerFragment drawerFragment;

    // Facebook variables
    ProfileTracker fbProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SyncService.setContext(getApplicationContext());

        // init tabs, pager, navigation, ImageButton OnClickListener
        initUI();
        // Bind service
        bindService(new Intent(this, SyncService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
        syncService = new SyncService();

        // Facebook initializations
        fbProfileTracker = new ProfileTracker(){
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            // On logout or login of facebook update profile;
                if(DEBUG) { Log.e("MainActivity", "Profile Changed"); }
                if( currentProfile != null )
                    updateProfileBasedOnNavDrawer();
            }
        };

    }

    @Override
    protected void onDestroy() {
        ////Log.e("Destroy", "Called");
        super.onDestroy();
        if ( SyncService.serviceRunning )
            syncService.stopService();

        // Unbind from the service
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }


        // Stop facebook tracking
        fbProfileTracker.stopTracking();
    }

    // Back button acts like home button
    public void onBackPressed() {
        if (drawerFragment.nowOpen) {
            drawerFragment.closeDrawer();
        } else moveTaskToBack(true);
    }

    protected void onResume() {
        super.onResume();
        recoverNavigationDrawer();
        //SyncService.myCard = getMyCard();
    }

    protected void onPause() {
        saveNavigationDrawer();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.ren.R.menu.menu_main, menu);
        // Bind is finished at this time
        MenuItem mi = menu.getItem(0);

        // New Ren Icon for connect
        if (SyncService.serviceRunning) {
            mi.setIcon(R.drawable.ren_orange);
        } else {
            mi.setIcon(R.drawable.ren_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();


        if (id == R.id.on_off_button) {
            syncService.menuItem = item;
            // currently not sending/serviceRunning
            if (!SyncService.serviceRunning) {

                // Upon stopping a "connect" session we want to flush the people that are currently ignored.
                // During a session if we ignore someone, they should not show up again for that sesssion.
                // The meaning of "Session": When you click on the "connect" button they are in a session. When turn off the "connect" the session ends.
                SyncService.clearIgnoredCards();

                Card c = getMyCard( false );
                if (c.getUname() == null || c.getUname().equals("")) {
                    return true;
                }
                syncService.setContext(getApplicationContext());
                EditText et = (EditText) findViewById(R.id.user_name);
                // Check BT, tell the user again if BT is not available
                if (et.getText().toString().isEmpty()) {
                    // Check name
                    Toast.makeText(this, getString(R.string.enter_name), Toast.LENGTH_LONG).show();
                    return true;
                }

                // Set the tab to "Contacts"
                bottomFragmentTabHost.setCurrentTab( CONTACT_TAB_INDEX );
                // Everything OK, request cards
                syncService.setMyCard(c);
                syncService.startService();
            }
            // power button is on, sending/serviceRunning is happening
            else {
                syncService.stopService();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_SELECTED:
                if(data != null) {
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setInitialCropWindowPaddingRatio(0)
                            .setRequestedSize(PHOTO_WIDTH, PHOTO_HEIGHT)
                            .start(this);
                }
                break;
            case PICK_CROP:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras == null) {
                        //Log.d("MainActivity", "ActivityResult::PICK_CROP");
                        break;
                    }
                    Bitmap selectedBitmap = extras.getParcelable("data");
                    ImageButton ib = (ImageButton) findViewById(R.id.user_photo_button);
                    ib.setImageBitmap(selectedBitmap);

                    // Save the custom photo, otherwise onResume will cancel changes
                    if (selectedBitmap != null) {
                        userPhotoStr = Card.encodeTobase64(selectedBitmap);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        // User photo is saved as String
                        editor.putString("Photo", userPhotoStr);
                        editor.apply();
                    }
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();

                    Bitmap resizedImage = BitmapFactory.decodeFile(resultUri.getPath());

                    ImageButton ib = (ImageButton) findViewById(R.id.user_photo_button);
                    ib.setImageBitmap(resizedImage);

                    if(DEBUG) { Log.e(TAG, "Cropped Image size: " + (resizedImage.getByteCount()/1000) + "kb"); }

                    // Save the custom photo, otherwise onResume will cancel changes
                    if (resizedImage!= null) {
                        userPhotoStr = Card.encodeTobase64(resizedImage);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        // User photo is saved as String
                        editor.putString("Photo", userPhotoStr);
                        editor.apply();
                    }

                } else if( resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Failed image cropping.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Method to save a logged in user and their saved cards locally for a specific user.
     * @author Alvin Truong
     * @daate 7/1/2016
     */
    private void saveCardsIntoSharedPrefForUser() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();

        String savedCardsInJson = gson.toJson( syncService.getSavedUnameCardPairs() );
        editor.putString( prefs.getString("Login uname", "") + "->SavedCards", savedCardsInJson);
        editor.apply();
    }

    private void saveNavigationDrawer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        EditText editText = (EditText) findViewById(R.id.user_name);
        editor.putString("Name", editText.getText().toString());

        editor.putString("Photo", userPhotoStr);

        IconEditText iet = (IconEditText) findViewById(R.id.user_phone);
        editor.putString("Phone", iet.getEditText().getText().toString());

        iet = (IconEditText) findViewById(R.id.email_address);
        editor.putString("Email", iet.getEditText().getText().toString());

        Profile fbProfile = Profile.getCurrentProfile();
        if( fbProfile != null )
            editor.putString("Facebook", fbProfile.getId());
        else
            editor.putString("Facebook", "");

        iet = (IconEditText) findViewById(R.id.instagram);
        if (iet != null)
            editor.putString("Instagram", iet.getEditText().getText().toString());

//        iet = (IconEditText) findViewById(R.id.website);
//        editor.putString("Website", iet.getEditText().getText().toString());

        editText = (EditText) findViewById(R.id.about_me);
        editor.putString("AboutMe", editText.getText().toString());

        editor.putString("Gender", userGender.toString());

        editor.apply();
    }

    // ========================================================
    // Facebook Remove/Add Button
    // ========================================================
    private void switchToFBLoginButton()
    {
        LoginButton fbButton = (LoginButton)findViewById( R.id.facebook_login_button );
        Button      fbRemoveProfile = (Button)findViewById(R.id.fb_remove_profile_button);

        fbButton.setVisibility(View.VISIBLE);
        fbRemoveProfile.setVisibility(View.GONE);
    }

    private void switchToRemoveProfileButton()
    {
        LoginButton fbButton = (LoginButton)findViewById( R.id.facebook_login_button );
        Button      fbRemoveProfile = (Button)findViewById(R.id.fb_remove_profile_button);

        fbButton.setVisibility(View.GONE);
        fbRemoveProfile.setVisibility(View.VISIBLE);
    }

    private void recoverNavigationDrawer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        EditText editText = (EditText) findViewById(R.id.user_name);
        String name = prefs.getString("Name", null);
        editText.setText(name);

        IconEditText iet = (IconEditText) findViewById(R.id.user_phone);
        editText = iet.getEditText();
        String phone = prefs.getString("Phone", null);
        editText.setText(phone);

        iet = (IconEditText) findViewById(R.id.email_address);
        editText = iet.getEditText();
        String email = prefs.getString("Email", null);
        editText.setText(email);

        String fbId = prefs.getString("Facebook", "");
        if( fbId.equals("") ) {
            switchToFBLoginButton();
        }
        else {
            switchToRemoveProfileButton();
        }

        iet = (IconEditText) findViewById(R.id.instagram);
        if (iet != null) {
            editText = iet.getEditText();
            String ig = prefs.getString("Instagram", null);
            editText.setText(ig);
        }

//        iet = (IconEditText) findViewById(R.id.website);
//        editText = iet.getEditText();
//        String website = prefs.getString("Website", null);
//        editText.setText(website);

        editText = (EditText) findViewById(R.id.about_me);
        String aboutMe = prefs.getString("AboutMe", null);
        editText.setText(aboutMe);

        ImageButton ib = (ImageButton) findViewById(R.id.user_photo_button);
        userPhotoStr = prefs.getString("Photo", "Default");
        if (!userPhotoStr.equals("Default")) {
            byte[] decodedByte = Base64.decode(userPhotoStr, 0);
            Bitmap b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            ib.setImageBitmap(b);
        } else {
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.usericon);
            ib.setImageBitmap(b);
        }

        String tempGender = prefs.getString("Gender", "UNKNOWN");
        ImageView iv = (ImageView) findViewById(R.id.genderIcon);
        switch (tempGender) {
            case "UNKNOWN":
                userGender = Gender.UNKNOWN;
                iv.setImageResource(0);
                break;
            case "MALE":
                userGender = Gender.MALE;
                iv.setImageResource(R.drawable.male);
                break;
            case "FEMALE":
                userGender = Gender.FEMALE;
                iv.setImageResource(R.drawable.female);
                break;
            default:
                userGender = Gender.UNKNOWN;
                iv.setImageResource(0);
                break;
        }
    }

    private void initUI() {
        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        assert getSupportActionBar() != null; // This solves the warning
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true); // Show the return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Return only one level.

        // Setup Fragment Tabhost
        bottomFragmentTabHost = (FragmentTabHost)findViewById( R.id.mainFragmentTabHost );
        bottomFragmentTabHost.setup( this, getSupportFragmentManager(), R.id.fragmentMainTabContent );

        // Fragment Tabhost Colors
        bottomFragmentTabHost.setBackgroundColor(getResources().getColor(R.color.bottomBarBGColor) );

        bottomFragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                for( int i = 0; i < bottomFragmentTabHost.getTabWidget().getChildCount(); ++i ) {
                    bottomFragmentTabHost.getTabWidget().getChildAt(i).setBackgroundColor( getResources().getColor(R.color.bottomBarBGColor) );
                    ((TextView)bottomFragmentTabHost.getTabWidget().getChildAt(i).findViewById( R.id.title )).setTextColor( getResources().getColor( R.color.appbarPrimaryTextColor ) );
                    ImageView currentTabImageView = (ImageView)bottomFragmentTabHost.getTabWidget().getChildAt(i).findViewById( R.id.icon );
                    switch( i ) {
                        case 0:
                           currentTabImageView.setImageResource( R.drawable.home_tab_icon_white);
                            break;
                        case 1:
                            currentTabImageView.setImageResource( R.drawable.contacts_tab_icon_white);
                            break;
                        case 2:
                            currentTabImageView.setImageResource( R.drawable.my_card_tab_icon_white );
                    }
                }

                View currentTabView = bottomFragmentTabHost.getCurrentTabView();
//                currentTabView.setBackgroundColor( getResources().getColor( R.color.primaryColor ) );
                TextView currentTabTextView = (TextView)currentTabView.findViewById( R.id.title );
                currentTabTextView.setTextColor( getResources().getColor( R.color.greenAccentColor ) );
                ImageView currentTabImageView = (ImageView)currentTabView.findViewById( R.id.icon );
                switch(bottomFragmentTabHost.getCurrentTab()) {
                    case 0:
                        currentTabImageView.setImageResource( R.drawable.home_tab_icon_orange);
                        break;
                    case 1:
                        currentTabImageView.setImageResource( R.drawable.contacts_tab_icon_orange );
                        break;
                    case 2:
                        currentTabImageView.setImageResource( R.drawable.my_card_tab_icon_orange );
                        break;
                }
            }
        });

        TabHost.TabSpec homeTab = bottomFragmentTabHost.newTabSpec( HOME_TAB_TAG );
        TabHost.TabSpec contactTab= bottomFragmentTabHost.newTabSpec( CONTACT_TAB_TAG );
        TabHost.TabSpec mycardTab= bottomFragmentTabHost.newTabSpec( MYCARD_TAB_TAG );

//        homeTab.setIndicator( "HOME" );
//        contactTab.setIndicator( "CONTACT" );
//        mycardTab.setIndicator( "MY CARD" );

        View tabWithHomeIconView = LayoutInflater.from(this).inflate( R.layout.fragment_tab_host_with_img,bottomFragmentTabHost.getTabWidget(), false );
        TextView tabText = ((TextView)tabWithHomeIconView.findViewById( R.id.title ));
        ImageView tabIcon = ((ImageView)tabWithHomeIconView.findViewById( R.id.icon) );
        tabText.setText( "HOME" );
        tabIcon.setImageResource( R.drawable.home_tab_icon_white);
        homeTab.setIndicator( tabWithHomeIconView );

        View tabWithContactIconView = LayoutInflater.from(this).inflate( R.layout.fragment_tab_host_with_img,bottomFragmentTabHost.getTabWidget(), false );
        tabText = ((TextView)tabWithContactIconView.findViewById( R.id.title ));
        tabIcon = ((ImageView)tabWithContactIconView.findViewById( R.id.icon) );
        tabText.setText( "CONTACTS" );
        tabIcon.setImageResource( R.drawable.contacts_tab_icon_white);
        contactTab.setIndicator( tabWithContactIconView );

        View tabWithMyCardIconView = LayoutInflater.from(this).inflate( R.layout.fragment_tab_host_with_img,bottomFragmentTabHost.getTabWidget(), false );
        tabText = ((TextView)tabWithMyCardIconView.findViewById( R.id.title ));
        tabIcon = ((ImageView)tabWithMyCardIconView.findViewById( R.id.icon) );
        tabText.setText( "MY CARD" );
        tabIcon.setImageResource( R.drawable.my_card_tab_icon_white);
        mycardTab.setIndicator( tabWithMyCardIconView );

        bottomFragmentTabHost.addTab( homeTab, HomeFragment.class, null );
        bottomFragmentTabHost.addTab( contactTab, ContactsFragment.class, null );
        bottomFragmentTabHost.addTab( mycardTab, MyCardFragment.class, null );

        // Navigation
        // Pass toolbar to navigation drawer
        // com.ren.R.id.fragment_navigation_drawer is the id of the root layout in activity_main.xml
        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        Button removeFBButton = (Button)drawerFragment.getView().findViewById(R.id.fb_remove_profile_button);

        // Set onclick listener
        removeFBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                updateProfileBasedOnNavDrawer();
            }
        });
        // Setup button listener for facebook for profile update

        // ImageButton
        ImageButton userPhotoButton = (ImageButton) findViewById(R.id.user_photo_button);
        userPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("MainActivity", "User photo clicked" );
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    selectImage();
                else
                    doCrop();
            }
        });

        userPhotoButton.setLongClickable(true);
        userPhotoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageView imageView = (ImageView) findViewById(R.id.genderIcon);
                switch (userGender) {
                    case UNKNOWN:
                        imageView.setImageResource(R.drawable.male);
                        userGender = Gender.MALE;
                        break;

                    case MALE:
                        imageView.setImageResource(R.drawable.female);
                        userGender = Gender.FEMALE;
                        break;

                    case FEMALE:
                        imageView.setImageResource(0);
                        userGender = Gender.UNKNOWN;
                        break;
                }
                return true;
            }
        });

        // Update profile button
        Button updateBtn = (Button) findViewById(R.id.update_profile);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send data to database
                updateProfileBasedOnNavDrawer();

                Toast.makeText( getApplicationContext(), "Profile updated..", Toast.LENGTH_SHORT).show();
            }
        });

        // We want login page first.
        Button logoutBtn = (Button) findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save cards for the user before clearing login name
                saveCardsIntoSharedPrefForUser();

                // Removed saved username
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor spEditor = sp.edit();
                spEditor.putString( "Login uname", "" );

                spEditor.apply();

                // Update profile just incase
                updateProfileBasedOnNavDrawer();

                // Clear Received Cards and Tab
                SyncService.clearReceivedCards();

                //Fixing saved contacts view with refresh issue by setting tab to home page
                // 0 = home page
                bottomFragmentTabHost.setCurrentTab( 0 );

                if ( SyncService.serviceRunning )
                    syncService.stopService();
                Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(i);
            }
        });

        // Starts application with login page only if not already logged in
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sp.getString("Login uname", "");
        if( username == null || username.equals("") ) {
            Intent i = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(i);
        }

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

    public Card getMyCard(boolean toServerUpdate) {
        if( toServerUpdate ) {
            // Possible Fix
            saveNavigationDrawer();

            recoverNavigationDrawer();
        }
        EditText editText = (EditText) findViewById(R.id.user_name);
        String name = editText.getText().toString();

        IconEditText iet = (IconEditText) findViewById(R.id.user_phone);
        String phone = iet.getText().toString();

        iet = (IconEditText) findViewById(R.id.email_address);
        String email = iet.getText().toString();

        Profile fbProfile = Profile.getCurrentProfile();
        String fb = "";
        if( fbProfile != null )
           fb = fbProfile.getId();

        iet = (IconEditText) findViewById(R.id.instagram);
        String ig = "";
        if (iet != null) {
            ig = iet.getText().toString();
        }

//        iet = (IconEditText) findViewById(R.id.website);
//        String website = iet.getText().toString();

        editText = (EditText) findViewById(R.id.about_me);
        String aboutMe = editText.getText().toString();

        if (!userPhotoStr.equals("Default")) {
            // User has custom photo
            ImageButton ib = (ImageButton) findViewById(R.id.user_photo_button);
            Bitmap customPhoto = ((BitmapDrawable) ib.getDrawable()).getBitmap();
            Log.d("MainActivity", (customPhoto == null ? "true" : "false") );
            userPhotoStr = Card.encodeTobase64(customPhoto);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String uName = prefs.getString("Login_uname", "");
        /*Log.e("GetMyCard", "Ready to update server.");
        Log.e("GetMyCard", "uName is: "+uName);*/
        return new Card(uName, name, userGender.toString(), userPhotoStr,
//                        phone, email, fb, ig, website, aboutMe);
                        phone, email, fb, ig, aboutMe);
    }

    /**
     * Generates an async task that will update the user profile on the database
     * based on current navigation drawer
     */
    public void updateProfileBasedOnNavDrawer()
    {
        BackgroundConn bckConn = new BackgroundConn(getApplicationContext());
        // Register process needs modification
        Card myCard = getMyCard( true );
        //String uName = BackgroundConn.USERNAME;
        Log.e(TAG, "Photo from card:" + myCard.getmPhotoEncoded());
        bckConn.execute("update_profile", myCard.getmName(), myCard.getmPhone(), myCard.getmEmail(), myCard.getmGender(),
                myCard.getmFacebook(), myCard.getmInstagram(),
//                myCard.getmWebsite(), myCard.getmOther(), myCard.getmPhotoEncoded(), myCard.getUname());
                myCard.getmOther(), myCard.getmPhotoEncoded(), myCard.getUname());

        // Updates My Card profile if the user is currently on that tab
        if( bottomFragmentTabHost.getCurrentTab() == MY_CARD_TAB_INDEX ) {
            bottomFragmentTabHost.setCurrentTab( HOME_TAB_INDEX );
            bottomFragmentTabHost.setCurrentTab( MY_CARD_TAB_INDEX );
        }

    }
    // ServiceConnection monitors the connection with the service
    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // client uses (IBinder) service to communicate with service
            SyncService.LocalBinder binder = (SyncService.LocalBinder) service;
            // bind after each onResume
            syncService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}
