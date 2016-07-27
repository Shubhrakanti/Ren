package com.ren;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BackgroundConn extends AsyncTask<String, Void, String> {
    private static final String TAG = "BackgroundConn";
    Context context;
    public static String USERNAME = null;
    private static final int DISTANCE = 100000000;
    private static String myLocationStr;
    // Strings to use for calling background conn
    public static final String OBTAIN_SAVED_USERS = "get_saved_users",
                                SAVE_USER_STR = "save_user",
                                REMOVE_USER_STR = "remove_user";

    // Strings used to identify json object
    private final String    SAVED_USERS_JSON_STR = "saved users",
                            NEARBY_USERS_JSON_STR = "nearby users";

    BackgroundConn(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        // Own database similar to previous
//        Log.d("BackgroundConn", "BackgroundConn executed" );
//        String login_url = "http://hero.x10host.com/login.php";
//        String register_url = "http://hero.x10host.com/register.php";
//        String updateGPS_and_connect_url = "http://hero.x10host.com/updateAndConnect.php";
//        String updateGPS_url = "http://hero.x10host.com/updateGPS.php";
//        String profile_update_url = "http://hero.x10host.com/profile_update.php";
//        String obtain_saved_user_url = "http://hero.x10host.com/saved_user_list.php";
//        String save_user_url = "http://hero.x10host.com/save_user.php";
//        String remove_user_url = "http://hero.x10host.com/remove_user.php";

        String login_url = "http://senteapps.x10host.com/login.php";
        String register_url = "http://senteapps.x10host.com/register.php";
        String updateGPS_and_connect_url = "http://senteapps.x10host.com/updateAndConnect.php";
        String updateGPS_url = "http://senteapps.x10host.com/updateGPS.php";
        String profile_update_url = "http://senteapps.x10host.com/profile_update.php";
        String obtain_saved_user_url = "http://senteapps.x10host.com/saved_user_list.php";
        String save_user_url = "http://senteapps.x10host.com/save_user.php";
        String remove_user_url = "http://senteapps.x10host.com/remove_user.php";

        //above string is ur local wamp server address. To access local server from other devices u have to make changes in WAMP
        //apache httpd.conf file.
        switch (type) {
            case "login":
                try {
                    String user_name = params[1];
                    String password = params[2];
                    USERNAME = user_name;
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
                            + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    ///////////////reading/////////////
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "register":
                try {
                    String username = params[1];
                    String password = params[2];

                    URL url = new URL(register_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +
                            "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    ///////////////reading/////////////
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //Log.d("Sente", "result is " + result);
                    //USERNAME = username;
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "update_GPS":
                try {
                    String gps = params[2];
                    myLocationStr = gps;
                    String username = params[1];
                    USERNAME = username;

                    URL url = new URL(updateGPS_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +
                            "&" + URLEncoder.encode("gps", "UTF-8") + "=" + URLEncoder.encode(gps, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    ///////////////reading/////////////
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "update_GPS_and_connect":
                try {
                    String gps = params[2];
                    myLocationStr = gps;
                    String username = params[1];
                    String distance_limit = params[3];
                    USERNAME = username;

                    URL url = new URL(updateGPS_and_connect_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") +
                            "&" + URLEncoder.encode("gps", "UTF-8") + "=" + URLEncoder.encode(gps, "UTF-8") +
                            "&" + URLEncoder.encode("range_limit", "UTF-8") + "=" + URLEncoder.encode( distance_limit, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    ///////////////reading/////////////
                    InputStream inputStream = httpURLConnection.getInputStream();
                    if(MainActivity.DEBUG) { Log.e(TAG, "Gathering nearby"); }
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line;
                    while ((SyncService.serviceRunning) && (line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    if(!SyncService.serviceRunning) {
                        Log.e(TAG, "Service cancelled" );
                        result = "";
                    }

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "update_profile":
                try {
                    String name = params[1];
                    String phone = params[2];
                    String email = params[3];
                    String gender = params[4];
                    String fb = params[5];
                    String ig = params[6];
//                    String website = params[7];
                    String aboutMe = params[7];
                    String photo = params[8];
                    String uName = params[9];
                    URL url = new URL(profile_update_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") +
                            "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") +
                            "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") +
                            "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8") +
                            "&" + URLEncoder.encode("femail", "UTF-8") + "=" + URLEncoder.encode(fb, "UTF-8") +
                            "&" + URLEncoder.encode("instagram", "UTF-8") + "=" + URLEncoder.encode(ig, "UTF-8") +
//                            "&" + URLEncoder.encode("website", "UTF-8") + "=" + URLEncoder.encode(website, "UTF-8") +
                            "&" + URLEncoder.encode("about_me", "UTF-8") + "=" + URLEncoder.encode(aboutMe, "UTF-8") +
                            "&" + URLEncoder.encode("photo", "UTF-8") + "=" + URLEncoder.encode(photo, "UTF-8") +
                            "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(uName, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    ///////////////reading/////////////
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //USERNAME = username;
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case OBTAIN_SAVED_USERS:
                try {
                    String username = params[1];

                    URL url = new URL(obtain_saved_user_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream httpOutputStream = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(httpOutputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

                    writer.write(post_data);
                    writer.flush();
                    writer.close();
                    httpOutputStream.close();

                    InputStream httpInputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(( new InputStreamReader(httpInputStream, "UTF-8")));
                    String result = "";
                    String line;
                    while((line = reader.readLine()) != null) {
                        result += line;
                    }
                    reader.close();
                    httpInputStream.close();

                    httpURLConnection.disconnect();
                    return result;
                } catch(IOException e ) {
                    e.printStackTrace();
                }
                break;

            case SAVE_USER_STR:
                try {
                    String username = params[1];
                    String save_username = params[2];

                    URL url = new URL(save_user_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream httpOutputStream = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(httpOutputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                            + "&" + URLEncoder.encode("save_username", "UTF-8") + "=" + URLEncoder.encode(save_username, "UTF-8");

                    writer.write(post_data);
                    writer.flush();
                    writer.close();
                    httpOutputStream.close();

                    InputStream httpInputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(( new InputStreamReader(httpInputStream, "UTF-8")));
                    String result = "";
                    String line;
                    while((line = reader.readLine()) != null) {
                        result += line;
                    }
                    reader.close();
                    httpInputStream.close();

                    httpURLConnection.disconnect();
                    return result;
                } catch(IOException e ) {
                    e.printStackTrace();
                }
                break;

            case REMOVE_USER_STR:
                try {
                    String username = params[1];
                    String remove_username = params[2];

                    URL url = new URL(remove_user_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream httpOutputStream = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(httpOutputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                            + "&" + URLEncoder.encode("remove_username", "UTF-8") + "=" + URLEncoder.encode(remove_username, "UTF-8");

                    writer.write(post_data);
                    writer.flush();
                    writer.close();
                    httpOutputStream.close();

                    InputStream httpInputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(( new InputStreamReader(httpInputStream, "UTF-8")));
                    String result = "";
                    String line;
                    while((line = reader.readLine()) != null) {
                        result += line;
                    }
                    reader.close();
                    httpInputStream.close();

                    httpURLConnection.disconnect();
                    return result;
                } catch(IOException e ) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //String TAG = "Connection";
        if (result == null)
            return;

        if(MainActivity.DEBUG) { Log.e(TAG, "Post Result: " + result ); }
        // If Json object call method to handle json else handle string
        try {
            JSONObject jsonObj = new JSONObject( result );
            jsonObjectRouter(jsonObj);
            return;
        } catch( JSONException e ) { }

        if (result.contains("login success")) {

            // Set username
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
            SharedPreferences.Editor sp_editor = sp.edit();
            sp_editor.putString( "Login uname", USERNAME );
            sp_editor.apply();

            // Populate navigation drawer
            saveLoginCardForNavigation( parseResultForCardData( result ) );

            Toast.makeText(context, context.getString(R.string.log_in_success), Toast.LENGTH_SHORT).show();
            if (context instanceof Activity) {
                ((Activity) context).onBackPressed();
            }
            // Auto populate
        } else if (result.contains("login failed")) {
            Toast.makeText(context, context.getString(R.string.log_in_fail), Toast.LENGTH_SHORT).show();
        } else if (result.contains("Login account created.")) {
            //Log.e(TAG, "Account created.");
            Toast.makeText(context, context.getString(R.string.new_account_created), Toast.LENGTH_SHORT).show();
        } else if (result.contains("Username already in use")) {
            Toast.makeText(context, context.getString(R.string.usr_name_in_use), Toast.LENGTH_SHORT).show();
        } else if (result.contains("gps updated")) {
//            Log.e("ServerResponse", "GPS updated.");
            //Log.e(TAG, "Parse cards here:");
            //Log.e(TAG, result);
            //update list of cards
        } else if (result.contains("profile updated")) {
//            Log.e("BCK", "profile updated"); // The echo is literally "profile updated"

        } else if (result.contains("profile not updated")) {
//            Log.e("BCK", "Profile not updated");
            //Log.e(TAG, "profile not updated");
        } else if( result.contains("user saved" ) ) {
//            Log.e( "BackgroundConn", "user saved");
        } else if( result.contains("user removed" ) ) {
//            Log.e( "BackgroundConn", "user removed" );
        }
    }


    /**
     * Determine which method to call based on json object's "title" key
     * @author Alvin Truong
     * @date 7/14/2016
     */
    public void jsonObjectRouter( JSONObject jsonObj )
    {
        String titleStr = "";
        try {
            titleStr = jsonObj.getString("title");
        } catch( JSONException e ) {}

        switch(titleStr) {
            case SAVED_USERS_JSON_STR:
                getAndSaveSavedUsersFromJson(jsonObj);
                setSavedCardAdapter();
                break;

            case NEARBY_USERS_JSON_STR:
                if(MainActivity.DEBUG) { Log.e(TAG, "JsonRouterNearby"); }
                getAndSaveNearbyUsersFromJson( jsonObj );
                break;
        }
    }

    /**
     *  Obtain usrs from json object, generate card, and add to received cards structure
     */
    private void getAndSaveNearbyUsersFromJson( JSONObject json )
    {
        JSONArray nearbyUsersJsonArray = json.optJSONArray( "nearby_users_list" );

        for( int row = 0; row < nearbyUsersJsonArray.length(); ++row ) {
            try {
                JSONObject jsonRow = nearbyUsersJsonArray.getJSONObject( row );
                Card nearbyUserCard = new Card(
                        jsonRow.optString("username"),
                        jsonRow.optString("name"),
                        jsonRow.optString("gender"),
                        jsonRow.optString("photo"),
                        jsonRow.optString("aboutme")
                );

                if(MainActivity.DEBUG) { Log.e( TAG, nearbyUserCard.getUname() + "-> " + ((nearbyUserCard.getmPhotoEncoded().getBytes().length)/1000) + "kb");
                                        }
                SyncService.addNewCard( nearbyUserCard );

            } catch( JSONException e ) { Log.e("bconn", "Json Error"); }
        }

        if(MainActivity.DEBUG) { Log.e(TAG, "Ignored cards" + SyncService.getIgnoredCards().toString()); }
    }

    /**
     * Parse json string of saved users and call method to save the saved username cards
     * @author Alvin Truong
     * @date 7/14/2016
     */
    private void getAndSaveSavedUsersFromJson( JSONObject json )
    {
        HashMap<String, Card> savedCards = new HashMap<>();

        JSONArray savedUserJsonArray = json.optJSONArray( "saved_users_list" );

        // Iterate through JSONArray to obtain JSONObject of users
        for (int i = 0; i < savedUserJsonArray.length(); ++i) {
            try {
                JSONObject row = savedUserJsonArray.getJSONObject(i);
                Card savedUserCard = new Card(
                        row.optString("username"),
                        row.optString("name"),
                        row.optString("gender"),
                        row.optString("photo"),
                        row.optString("phone"),
                        row.optString("email"),
                        row.optString("facebook"),
                        row.optString("instagram"),
//                        row.optString("website"),
                        row.optString("aboutme")
                );

                savedUserCard.setmSaved(true);
                savedCards.put(savedUserCard.getUname(), savedUserCard);
            } catch (JSONException e) {
            }
        }
        SyncService.setSavedUnameCardPairs( savedCards );
    }

    /**
     * Sets the saved card list to the savedcardadapter so that it will display most up to date information
     * from the recylcerview
     */
    private void setSavedCardAdapter()
    {
        TabFragment.savedCardAdapter.setCardList( SyncService.getSavedCards() );
    }

    /** Parses the result from "login success" to extract the meaningful data to construct a Card.
     *
     * @param result
     *          contains a string in the format of "login success hero!@#$ 1111111!@#$ email!@#$ MALE!@#$ facebook!@#$ instagram!@#$ aboutme!@#$ Default<br>"
     *
     * @return String[] consisting only have the meaningful data in between "!@#$ "
     * @author Alvin Truong
     * @date   6/25/2016
     */
    private String[] parseResultForCardData( String result ) {
        if( result == null ) { return null; };

        // Split up data and call setMyCard to setup navigation drawer with information.
        String removedStatusMessage = result.replace( "login success ", "");
        String[] splitForMeaningfulData = removedStatusMessage.split( "!@#\\$ " );
        splitForMeaningfulData[7] = splitForMeaningfulData[7].replace("<br>", "");

        return splitForMeaningfulData;
    }

    /** Saves card data in shared memory to auto populate navigation drawer in MainActivity.
     *
     * @param cardData: Format =>
     *         0       1       2       3       4           5           6           7
     *       ( name,   phone,  email,  gender, facebook,   instagram,  aboutme,    photo )
     *
     * @return Card if cardData is provided else null
     * @author Alvin Truong
     * @date   6/25/2016
     */
    private void saveLoginCardForNavigation( String[] cardData ) {
        if( cardData == null ) { return; }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("Name", cardData[0]);

        if( cardData[7].equals( "" ) )
            editor.putString("Photo", "Default");
        else
            editor.putString("Photo", cardData[7] );

        editor.putString("Phone", cardData[1]);

        editor.putString("Email", cardData[2]);

        editor.putString("Facebook", cardData[4] );

        editor.putString("Instagram", cardData[5] );

//        editor.putString("Website", cardData[6] );

        editor.putString("AboutMe", cardData[6] );

        editor.putString("Gender", cardData[3] );

        editor.apply();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
    }

}
