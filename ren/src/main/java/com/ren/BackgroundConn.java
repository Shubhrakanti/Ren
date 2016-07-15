package com.ren;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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
    Context context;
    public static String USERNAME = null;
    private static final int DISTANCE = 100000000;
    private static String myLocationStr;
    // Strings to use for calling background conn
    public static final String OBTAIN_SAVED_USERS = "get_saved_users",
                                SAVE_USER_STR = "save_user",
                                REMOVE_USER_STR = "remove_user";

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
                    //Log.d("Sente", "username is " + user_name + " password is " + password);
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
//                        Log.e("BackgroundConn", line);
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
//                    Log.e("BackgroundConn", "result is " + result);
                    //this is global username for next intent
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
                    //String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                    //      +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
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
//                    while ((line = bufferedReader.readLine()) != null) {
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //USERNAME = username;
//                    Log.d("BackgroundConn", "Updating database location with " + myLocationStr );
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
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //USERNAME = username;
//                    Log.d("BackgroundConn", "Updating database location with " + myLocationStr );
                    //Log.d( "BackgroundConn", "Result->" + result );
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "update_profile":
                try {
                    //Log.d("BackgroundConn", "Updating profile");
                    String name = params[1];
                    String phone = params[2];
                    String email = params[3];
                    String gender = params[4];
                    String fb = params[5];
                    String ig = params[6];
                    String website = params[7];
                    String aboutMe = params[8];
                    String photo = params[9];
                    String uName = params[10];
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
                            "&" + URLEncoder.encode("website", "UTF-8") + "=" + URLEncoder.encode(website, "UTF-8") +
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

        // If Json object call method to handle json else handle string
        try {
//            Log.e( "BackgroundConn", result );
            JSONObject jsonObj = new JSONObject( result );
            jsonObjectRouter(jsonObj);
            return;
        } catch( JSONException e ) { }

        if (result.contains("login success")) {
            //Log.e(TAG, "Log in success.");
            //Log.e("My profile", result);

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
            getUsers(result);
            //update list of cards
        } else if (result.contains("profile updated")) {
            //Log.e(TAG, "profile updated"); // The echo is literally "profile updated"

        } else if (result.contains("profile not updated")) {
            //Log.e(TAG, "profile not updated");
        } else if( result.contains("user saved" ) ) {
//            Log.e( "BackgroundConn", "user saved");
        } else if( result.contains("user removed" ) ) {
//            Log.e( "BackgroundConn", "user removed" );
        }
    }

    private void getUsers(String str) {
        str = str.replace("gps updated ", "");
        String[] rows = str.split("<br>");

        Location myLocation = new Location("MyLocation");
        String[] gps = myLocationStr.split(",");
        if( gps[0]!=null &&  gps[1]!=null) {
            myLocation.setLatitude(Double.parseDouble(gps[0]));
//            myLocation.setLatitude(Double.parseDouble(gps[1]));
            myLocation.setLongitude(Double.parseDouble(gps[1]));
        }

        for (String s : rows) {
            try {
                //Log.d("Background", "getUsers string: " + s);
                List<String> fields = new ArrayList<>();
                while (s.contains("<li>")) {
                    String currentField = s.substring(0, s.indexOf("<li>"));
                    s = s.substring(currentField.length() + 4);
                    fields.add(currentField);
                    //Log.e("AddingField", currentField);
                }
                if (fields.get(1).replaceFirst(" ", "").equals(USERNAME)) {
                    continue;
                }
                Location usrLocation = new Location("UsrLocation");
                gps = fields.get(0).split(",");
                try {
                    usrLocation.setLatitude(Double.parseDouble(gps[0]));
//                usrLocation.setLatitude(Double.parseDouble(gps[1]));
                    usrLocation.setLongitude(Double.parseDouble(gps[1]));
                } catch (NumberFormatException e) {
                    continue;
                }
            /*if(usrLocation.distanceTo(myLocation)>DISTANCE) {
                continue;
            }*/
                Card receivedCard = new Card(
                        fields.get(1).replaceFirst(" ", ""),
                        fields.get(2).replaceFirst(" ", ""),
                        fields.get(3).replaceFirst(" ", ""),
                        fields.get(4).replaceFirst(" ", ""),
                        fields.get(5).replaceFirst(" ", ""),
                        fields.get(6).replaceFirst(" ", ""),
                        fields.get(7).replaceFirst(" ", ""),
                        fields.get(8).replaceFirst(" ", ""),
                        fields.get(9).replaceFirst(" ", ""),
                        fields.get(10).replaceFirst(" ", "")
                );
                SyncService.addNewCard(receivedCard);
            } catch( IndexOutOfBoundsException e ) {
//                Log.d( "BackgroundConn", "Out of bound" );
            }
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
            case "saved users":
                getSavedUsersFromJson(jsonObj);
                setSavedCardAdapter();
                break;
        }
    }

    /**
     * Parse json string of saved users and call method to save the saved username cards
     * @author Alvin Truong
     * @date 7/14/2016
     */
    public void getSavedUsersFromJson( JSONObject json )
    {
        HashMap<String, Card> savedCards = new HashMap<>();

        JSONArray savedUserJsonArray = json.optJSONArray( "saved_users_list" );

        // Iterate through JSONArray to obtain JSONObject of users
        for (int i = 0; i < savedUserJsonArray.length(); ++i) {
            try {
                JSONObject row = savedUserJsonArray.getJSONObject(i);
                Card savedUserCard = new Card(
                        row.optString("username", "ERROR"),
                        row.optString("name", "ERROR"),
                        row.optString("gender", "ERROR"),
                        row.optString("photo", "ERROR"),
                        row.optString("phone", "ERROR"),
                        row.optString("email", "ERROR"),
                        row.optString("facebook", "ERROR"),
                        row.optString("instagram", "ERROR"),
                        row.optString("website", "ERROR"),
                        row.optString("aboutme", "ERROR")
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
    public void setSavedCardAdapter()
    {
        TabFragment.savedCardAdapter.setCardList( SyncService.getSavedCards() );
    }

    /** Parses the result from "login success" to extract the meaningful data to construct a Card.
     *
     * @param result
     *          contains a string in the format of "login success hero!@#$ 1111111!@#$ email!@#$ MALE!@#$ facebook!@#$ instagram!@#$ website!@#$ aboutme!@#$ Default<br>"
     *
     * @return String[] consisting only have the meaningful data in between "!@#$ "
     * @author Alvin Truong
     * @date   6/25/2016
     */
    private String[] parseResultForCardData( String result ) {
        if( result == null ) { return null; };

        // Split up data and call setMyCard to setup navigation drawer with information.
        String removedStatusMessage = result.replace( "login success ", "");
        //Log.d("BackgroundConn", "Original result: '" + result + "'" );
        //Log.d("BackgroundConn", "Replaced message: '" + removedStatusMessage +"'");
        String[] splitForMeaningfulData = removedStatusMessage.split( "!@#\\$ " );
        splitForMeaningfulData[8] = splitForMeaningfulData[8].replace("<br>", "");


//         Prints out debug message for parsed data.
//        Log.d("BackgroundConn", "Parsed Length: " + splitForMeaningfulData.length );
//        for( String data : splitForMeaningfulData ) {
//            Log.d("BackgroundConn", "Parsed Data: " + data );
//        }
        return splitForMeaningfulData;
    }

    /** Saves card data in shared memory to auto populate navigation drawer in MainActivity.
     *
     * @param cardData: Format =>
     *         0       1       2       3       4           5           6           7           8
     *       ( name,   phone,  email,  gender, facebook,   instagram,  website,    aboutme,    photo )
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

        if( cardData[8].equals( "" ) )
            editor.putString("Photo", "Default");
        else
            editor.putString("Photo", cardData[8] );

        editor.putString("Phone", cardData[1]);

        editor.putString("Email", cardData[2]);

        editor.putString("Facebook", cardData[4] );

        editor.putString("Instagram", cardData[5] );

        editor.putString("Website", cardData[6] );

        editor.putString("AboutMe", cardData[7] );

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