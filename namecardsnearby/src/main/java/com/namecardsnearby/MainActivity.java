package com.namecardsnearby;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kylewbanks.android.iconedittext.IconEditText;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    // Crop
    private static final int PICK_CROP = 100;
    // For service
    private SyncService syncService;
    private boolean isBound = false;
    // Custom photo?
    private String userPhotoStr = "Default";

    // Gender
    private enum Gender {
        UNKNOWN, MALE, FEMALE
    }

    private Gender userGender = Gender.UNKNOWN;
    // Navigation
    private NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init tabs, pager, navigation, ImageButton OnClickListener
        initUI();
        // Bind service
        bindService(new Intent(this, SyncService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
        syncService = new SyncService();
    }

    @Override
    protected void onDestroy() {
        ////Log.e("Destroy", "Called");
        super.onDestroy();
        // Unbind from the service
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
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
        getMenuInflater().inflate(com.namecardsnearby.R.menu.menu_main, menu);
        // Bind is finished at this time
        MenuItem mi = menu.getItem(0);
        if (SyncService.serviceRunning) {
            mi.setIcon(R.drawable.onoff_green);
        } else {
            mi.setIcon(R.drawable.onoff_gray);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.on_off_button) {
            syncService.menuItem = item;
            // current not sending/serviceRunning
            if (!SyncService.serviceRunning) {
                Card c = getMyCard();
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
            case PICK_CROP:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras == null) {
                        break;
                    }
                    Bitmap selectedBitmap = extras.getParcelable("data");
                    ImageButton ib = (ImageButton) findViewById(R.id.user_photo_button);
                    ib.setImageBitmap(selectedBitmap);
                    // Save the custom photo, otherwise onResume will cancel changes
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (selectedBitmap != null) {
                        selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] b = baos.toByteArray();
                        userPhotoStr = Base64.encodeToString(b, Base64.DEFAULT);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        // User photo is saved as String
                        editor.putString("Photo", userPhotoStr);
                        editor.apply();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

        iet = (IconEditText) findViewById(R.id.facebook_account);
        if (iet != null)
            editor.putString("Facebook", iet.getEditText().getText().toString());

        iet = (IconEditText) findViewById(R.id.instagram);
        if (iet != null)
            editor.putString("Instagram", iet.getEditText().getText().toString());

        iet = (IconEditText) findViewById(R.id.website);
        editor.putString("Website", iet.getEditText().getText().toString());

        editText = (EditText) findViewById(R.id.about_me);
        editor.putString("AboutMe", editText.getText().toString());

        editor.putString("Gender", userGender.toString());

        Gson gson = new Gson();
        String json = gson.toJson(syncService.getSavedUnameCardPairs());
        editor.putString("SavedCardJson", json);

        editor.apply();
    }

    private void recoverNavigationDrawer() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String json = prefs.getString("SavedCardJson", null);
        if (json != null) {
            Type type = new TypeToken<HashMap<String, Card>>() {
            }.getType();
            Gson gson = new Gson();
            HashMap<String, Card> savedOnes = gson.fromJson(json, type);
            syncService.setSavedUnameCardPairs(savedOnes);
        }

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

        iet = (IconEditText) findViewById(R.id.facebook_account);
        if (iet != null) {
            editText = iet.getEditText();
            String fb = prefs.getString("Facebook", null);
            editText.setText(fb);
        }

        iet = (IconEditText) findViewById(R.id.instagram);
        if (iet != null) {
            editText = iet.getEditText();
            String ig = prefs.getString("Instagram", null);
            editText.setText(ig);
        }

        iet = (IconEditText) findViewById(R.id.website);
        editText = iet.getEditText();
        String website = prefs.getString("Website", null);
        editText.setText(website);

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
        Toolbar toolbar = (Toolbar) findViewById(com.namecardsnearby.R.id.app_bar);
        assert getSupportActionBar() != null; // This solves the warning
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true); // Show the return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Return only one level.
        // tabs and pager
        ViewPager mPager;
        mPager = (ViewPager) findViewById(R.id.pager); // Pager is the area where recyclerview shows
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), this));

        AtomicReference<SlidingTabLayout> mTabs = new AtomicReference<>();
        mTabs.set((SlidingTabLayout) findViewById(R.id.tabs));
        mTabs.get().setDistributeEvenly(true);
        mTabs.get().setViewPager(mPager);
        mTabs.get().setBackgroundColor(getResources().getColor(R.color.primaryColor));
        mPager.setCurrentItem( 1 );

        // Navigation
        // Pass toolbar to navigation drawer
        // com.namecardsnearby.R.id.fragment_navigation_drawer is the id of the root layout in activity_main.xml
        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(com.namecardsnearby.R.id.fragment_navigation_drawer);
        drawerFragment.setUp(com.namecardsnearby.R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(com.namecardsnearby.R.id.drawer_layout), toolbar);

        // ImageButton
        ImageButton userPhotoButton = (ImageButton) findViewById(R.id.user_photo_button);
        userPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        /*// Account Activity
        Button accountBtn = (Button) findViewById(R.id.account);
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(i);
            }
        });*/

        // Update profile button
        Button updateBtn = (Button) findViewById(R.id.update_profile);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundConn bckConn = new BackgroundConn(getApplicationContext());
                // Register process needs modification
                Card myCard = getMyCard();
                //String uName = BackgroundConn.USERNAME;
                bckConn.execute("update_profile", myCard.getmName(), myCard.getmPhone(), myCard.getmEmail(), myCard.getmGender(),
                        myCard.getmFacebook(), myCard.getmInstagram(),
                        myCard.getmWebsite(), myCard.getmOther(), myCard.getmPhotoEncoded(), myCard.getUname());
                Toast.makeText(getApplicationContext(), "Profile updated..", Toast.LENGTH_SHORT).show();
            }
        });

        // We want login page first.
        Button logoutBtn = (Button) findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Removed saved username
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor spEditor = sp.edit();
                spEditor.putString( "Login uname", "" );

                spEditor.apply();

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
        startActivityForResult(photoPickerIntent, PICK_CROP);
    }

    public Card getMyCard() {
        EditText editText = (EditText) findViewById(R.id.user_name);
        String name = editText.getText().toString();

        IconEditText iet = (IconEditText) findViewById(R.id.user_phone);
        String phone = iet.getText().toString();

        iet = (IconEditText) findViewById(R.id.email_address);
        String email = iet.getText().toString();

        iet = (IconEditText) findViewById(R.id.facebook_account);
        String fb = "";
        if (iet != null) { // There was a layout for Chinese
            fb = iet.getText().toString();
        }

        iet = (IconEditText) findViewById(R.id.instagram);
        String ig = "";
        if (iet != null) {
            ig = iet.getText().toString();
        }

        iet = (IconEditText) findViewById(R.id.website);
        String website = iet.getText().toString();

        editText = (EditText) findViewById(R.id.about_me);
        String aboutMe = editText.getText().toString();

        if (!userPhotoStr.equals("Default")) {
            // User has custom photo
            ImageButton ib = (ImageButton) findViewById(R.id.user_photo_button);
            Bitmap customPhoto = ((BitmapDrawable) ib.getDrawable()).getBitmap();
            userPhotoStr = Card.encodeTobase64(customPhoto);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String uName = prefs.getString("Login_uname", "");
        /*Log.e("GetMyCard", "Ready to update server.");
        Log.e("GetMyCard", "uName is: "+uName);*/
        return new Card(uName,
                name, userGender.toString(), userPhotoStr, phone, email, fb, ig, website, aboutMe);
    }

    /** Populates navigation drawer with "successful login" data.
     *
     * @param myCard contains the card with the details associated with person logging in.
     * @author Alvin Truong
     * @date   6/25/2016
     */
    /*public void setMyCard( Card myCard ) {

        EditText editTextView = (EditText) findViewById( R.id.user_name );
        editTextView.setText( myCard.getmName() );

        IconEditText iconEditTextView = (IconEditText) findViewById( R.id.user_phone );
        iconEditTextView.getEditText().setText( myCard.getmPhone() );

        iconEditTextView = (IconEditText) findViewById( R.id.email_address );
        iconEditTextView.getEditText().setText( myCard.getmEmail() );

        iconEditTextView = (IconEditText) findViewById( R.id.facebook_account );
        iconEditTextView.getEditText().setText( myCard.getmFacebook() );

        iconEditTextView = (IconEditText) findViewById( R.id.instagram );
        iconEditTextView.getEditText().setText( myCard.getmInstagram() );

        iconEditTextView = (IconEditText) findViewById( R.id.website );
        iconEditTextView.getEditText().setText( myCard.getmWebsite() );

        editTextView = (EditText) findViewById( R.id.about_me );
        editTextView.setText( myCard.getmOther() );

        // Set gender image
        ImageView imageView = (ImageView) findViewById(R.id.genderIcon);
        switch( myCard.getmGender() ) {
            case "MALE":
                imageView.setImageResource( R.drawable.male );
                break;

            case "FEMALE":
                imageView.setImageResource( R.drawable.female );
                break;

            default:
                imageView.setImageResource( 0 );
                break;
        }

        // Set profile image
        ImageButton ib = (ImageButton) findViewById(R.id.user_photo_button);
        if (!myCard.getmPhotoEncoded().equals("Default")) {
            // User has custom photo
            Bitmap customPhoto = myCard.decodeBase64();
            ib.setImageBitmap( customPhoto );
        }
        else {
            ib.setImageResource( R.drawable.usericon );
        }
    }
    */

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
