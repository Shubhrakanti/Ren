package com.ren;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kylewbanks.android.iconedittext.IconEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    public static final String PREF_FILE_NAME = "userinfo";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    public boolean nowOpen = false; // For onBackPressed() in main activity

    // Facebook
    CallbackManager callbackManager;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }

        // Facebook initializations
        FacebookSdk.sdkInitialize( getContext() );
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(com.ren.R.layout.fragment_navigation_drawer, container, false);

        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        // Set Facebook permissions we are requesting
        fbLoginButton.setReadPermissions("public_profile");
        fbLoginButton.setFragment( this );

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Only change the profile for most currently logged in person.
                if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            //currentProfile == the new profile
                            Profile.setCurrentProfile( currentProfile );
                            Profile user_profile = Profile.getCurrentProfile();
                            if (MainActivity.DEBUG) {
                                Log.e("NavDrawer", "We did it boys: " + user_profile.getId());
                            }
                            Toast.makeText(getActivity(), "We did it boys: " + user_profile.getId(), Toast.LENGTH_SHORT).show();
                            mProfileTracker.stopTracking();
                        }
                    };
                }
            }

            @Override
            public void onCancel() {
                if(MainActivity.DEBUG) { Log.e("NavDrawer", "FB Cancelled"); }
            }

            @Override
            public void onError(FacebookException error) {
                if(MainActivity.DEBUG) { Log.e("NavDrawer", "FB Login Error"); }
            }
        });

        return view;
    }

    public void setUp(int fragmentId, final DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, com.ren.R.string.draw_open, com.ren.R.string.draw_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                nowOpen = true;
                //Log.d("NavigationDrawerFragment", "Drawer Opened");
                // getActivity().invalidateOptionsMenu(); // On/Off button will be messed
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                nowOpen = false;
                hide_keyboard(getActivity());
                //Log.d("NavigationDrawerFragment", "Drawer Closed");
                //getActivity().invalidateOptionsMenu();
            }

        };
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState(); // For the hamburger
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //    /** Populates navigation drawer with "successful login" data.
//     *
//     * @param myCard contains the card with the details associated with person logging in.
//     * @author Alvin Truong
//     * @date   6/25/2016
//     */
//    public void setMyCard( Card myCard ) {
//
//        EditText editTextView = (EditText) getView().findViewById( R.id.user_name );
//        editTextView.setText( myCard.getmName() );
//
//        IconEditText iconEditTextView = (IconEditText) getView().findViewById( R.id.user_phone );
//        iconEditTextView.getEditText().setText( myCard.getmPhone() );
//
//        iconEditTextView = (IconEditText) getView().findViewById( R.id.email_address );
//        iconEditTextView.getEditText().setText( myCard.getmEmail() );
//
//        iconEditTextView = (IconEditText) getView().findViewById( R.id.facebook_account );
//        iconEditTextView.getEditText().setText( myCard.getmFacebook() );
//
//        iconEditTextView = (IconEditText) getView().findViewById( R.id.instagram );
//        iconEditTextView.getEditText().setText( myCard.getmInstagram() );
//
//        iconEditTextView = (IconEditText) getView().findViewById( R.id.website );
//        iconEditTextView.getEditText().setText( myCard.getmWebsite() );
//
//        editTextView = (EditText) getView().findViewById( R.id.about_me );
//        editTextView.setText( myCard.getmOther() );
//
//        // Set gender image
//        ImageView imageView = (ImageView) getView().findViewById(R.id.genderIcon);
//        switch( myCard.getmGender() ) {
//            case "MALE":
//                imageView.setImageResource( R.drawable.male );
//                break;
//
//            case "FEMALE":
//                imageView.setImageResource( R.drawable.female );
//                break;
//
//            default:
//                imageView.setImageResource( 0 );
//                break;
//        }
//
//        // Set profile image
//        ImageButton ib = (ImageButton) getView().findViewById(R.id.user_photo_button);
//        if (!myCard.getmPhotoEncoded().equals("Default")) {
//            // User has custom photo
//            Bitmap customPhoto = myCard.decodeBase64();
//            ib.setImageBitmap( customPhoto );
//        }
//        else {
//            ib.setImageResource( R.drawable.usericon );
//        }
//    }
    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        //nowOpen = false;
    }
}
