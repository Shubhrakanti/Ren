package com.ren;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Card implements Serializable, Comparable<Card> {
    private static final String TAG = "Card";

    private String uName;
    private String mName; // This is the user's real name
    private String mPhotoEncoded;
    private String mPhone;
    private String mEmail;
    private String mFacebook;
    private String mInstagram;
//    private String mWebsite;
    private String mOther;
    private String mGender;
    private boolean mSaved = false;
    private boolean mIgnored = false;

    public Card() {
        uName = "";
        mName = "";
        mGender = "UNKNOWN";
        mPhotoEncoded = "Default";
        mPhone = "";
        mEmail = "";
        mFacebook = "";
        mInstagram = "";
//        mWebsite = "";
        mOther = "";
    }

    public Card( String u, String name, String gender, String photo, String aboutm) {
        this();

        uName = u;
        mName = name;
        mGender = gender;
        mPhotoEncoded = (photo.equals(" "))||(photo.equals("")) ? "Default" : photo;
        mOther = aboutm;
    }

    public Card(String u, String name, String gender, String photo, String phone, String email, String fb, String ig,
//                String website, String other) {
                String other) {
        uName = u;
        mName = name;
        mGender = (gender.equals(" "))||(gender.equals("")) ? "UNKNOWN":gender;
        mPhotoEncoded = (photo.equals(" "))||(photo.equals("")) ? "Default" : photo;
        mPhone = phone;
        mEmail = email;
        mFacebook = fb;
        mInstagram = ig;
//        mWebsite = website;
        mOther = other;
    }

    public static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        if(MainActivity.DEBUG) { Log.e(TAG, "Image size after compression: " + (baos.size()/1000) + "kb"); }
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap decodeBase64() {
        if (mPhotoEncoded != null && mPhotoEncoded.equals("Default")) {
            return (BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.usericon));
        }
//        Log.e("length of this pic", ""+mPhotoEncoded.length());
        byte[] decodedByte = Base64.decode(mPhotoEncoded, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public String getUname() {
        return uName;
    }

    public String getmName() {
        return mName;
    }

    public String getmPhotoEncoded() {
        return mPhotoEncoded;
    }

    public String getmPhone() {
        return mPhone;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmFacebook() {
        return mFacebook;
    }

    public String getmInstagram() {
        return mInstagram;
    }

//    public String getmWebsite() {
//        return mWebsite;
//    }

    public String getmOther() {
        return mOther;
    }

    public String getmGender() {
        return mGender;
    }

    public boolean ismSaved() {
        return mSaved;
    }

    public void setmSaved(boolean mSaved) {
        this.mSaved = mSaved;
    }

    public boolean ismIgnored() { return mIgnored; }

    public void setmIgnored(boolean mIgnored) {
        this.mIgnored = mIgnored;
    }

    // Override compareTo for Collections.sort()

    @Override
    public int compareTo(Card another) {
        return mName.compareToIgnoreCase(another.getmName());
    }
}
