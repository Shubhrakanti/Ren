package com.namecardsnearby;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
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

    BackgroundConn(Context ctx) {
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        // Own database similar to previous
        String login_url = "http://hero.x10host.com/login.php";
        String register_url = "http://hero.x10host.com/register.php";
        String updateGPS_url = "http://hero.x10host.com/update.php";
        String profile_update_url = "http://hero.x10host.com/profile_update.php";

//        String login_url = "http://senteapps.x10host.com/login.php";
//        String register_url = "http://senteapps.x10host.com/register.php";
//        String updateGPS_url = "http://senteapps.x10host.com/update.php";
//        String profile_update_url = "http://senteapps.x10host.com/profile_update.php";

        //above string is ur local wamp server address. To access local server from other devices u have to make changes in WAMP
        //apache httpd.conf file.
        switch (type) {
            case "login":
                try {
                    String user_name = params[1];
                    String password = params[2];
                    USERNAME = user_name;
                    //Log.d("Sente", "inside try");
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
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    //Log.d("Sente", "result is " + result);
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
                    Log.d("BackgroundConn", "Updating database location with " + myLocationStr );
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
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //String TAG = "Connection";
        if (result == null)
            return;
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
            Log.e("ServerResponse", "GPS updated.");
            //Log.e(TAG, "Parse cards here:");
            //Log.e(TAG, result);
            getUsers(result);
            //update list of cards
        } else if (result.contains("profile updated")) {
            //Log.e(TAG, "profile updated"); // The echo is literally "profile updated"

        } else if (result.contains("profile not updated")) {
            //Log.e(TAG, "profile not updated");
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
        }
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