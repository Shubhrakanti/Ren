package com.ren;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SyncService extends Service {

    private String max_dist = (MainActivity.DEBUG) ? "1000" : "2"; // in miles
    String TAG = "SyncService";
    public MenuItem menuItem;// To control the on/off button
    public static boolean serviceRunning = false;

    private static Context context;
    private static final int NOTIFICATION = 638;
//    private static final int TEN_SECS = 10 * 1000 * 1; // Stop requesting
    private static final int TEN_MINS = 60 * 1000 * 10; // Stop requesting
    private static final int REQUESTINTERVAL = 1000 * 10; // Request server
    // Location significantly newer
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private static Card myCard = new Card(); // Will be used when requesting update from server

    // User name and card pairs
    private static HashMap<String, Card> uNameCardPairs = new HashMap<>();
    private static HashMap<String, Card> savedUnameCardPairs = new HashMap<>();
    private static HashMap<String, Card> tempRemovedUNameCardPairs = new HashMap<>();
    private static HashMap<String, Card> ignoredUNameCardPairs = new HashMap<>();

    // Instance of LocalBinder
    private final IBinder myBinder = new LocalBinder();
    private PendingIntent pendingOff;
    private PendingIntent pendingRequestServer;
    private AlarmManager alarmManager;
    private NotificationManager mNotificationManager;

    // Location stuff
    private Location currentLocation;
    private LocationManager locationManager = null;
    private static final int MIN_TIME = 1000 * 1;
    private static final int MIN_DIST = 10;

    // "null" coordinates
    // Currently MT. EVEREST COORDINATES
    // +27.9878° N, +86.9250° E
    private final String    NULL_LATITUDE = "27.9878",
                            NULL_LONGITUDE = "86.9250";

    public SyncService() {
    }

    public static void setContext(Context c) {
        context = c;
        //Log.e("SyncService", c.toString());
    }

//    private void addDummy() {
//        Card dummy1 = new Card("dummy1", "Dummy1", "MALE", "Default", "5203018949", "dummy1@email.com", "100003657948716", "",
//                "dummy1.com", "hello guys!");
//        Card dummy2 = new Card("dummy2", "Dummy2", "FEMALE", "Default", "", "dummy2@email.com", "", "dp2m.s4",
//                "dummy2.com", "I'm dummy2");
//        uNameCardPairs.put("dummy1", dummy1);
//        uNameCardPairs.put("dummy2", dummy2);
//        List<Card> receivedCards = new ArrayList<>(uNameCardPairs.values()); // For dummies
//        TabFragment.newReceivedCardAdapter.setCardList(receivedCards); // For dummies
//    }

    public void setMyCard(Card c) {
        myCard = c;
    }

    public HashMap<String, Card> getSavedUnameCardPairs() {
        return savedUnameCardPairs;
    }

    /**
     * Modified setSavedUNameCardPairs in order to handle the problem of "removing a saved card as data is being fetched
     * from database".
     * @param savedOnes
     */
    public static void setSavedUnameCardPairs(HashMap<String, Card> savedOnes) {
        if( tempRemovedUNameCardPairs.size() > 0 ) {
            for( String key : tempRemovedUNameCardPairs.keySet() ) {
               savedOnes.remove( key );
            }
            tempRemovedUNameCardPairs.clear();
        }

        savedUnameCardPairs = savedOnes;
    }

    public static List<Card> getSavedCards() {
        return new ArrayList<>(savedUnameCardPairs.values());
    }

    public static List<Card> getReceivedCards() {
        return new ArrayList<>(uNameCardPairs.values());
    }

    /** Returns a list of Cards used to setup CardAdapter for ignored cards.
     */
    public static List<Card> getIgnoredCards() { return new ArrayList<>( ignoredUNameCardPairs.values() ); }

    /** Static functions to clear HashMap data structures
     *
     */
    public static void clearReceivedCards() { uNameCardPairs.clear(); }
    public static void clearSavedCards() { savedUnameCardPairs.clear(); }
    public static void clearIgnoredCards() { ignoredUNameCardPairs.clear(); }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // Create a BroadcastReceiver for Bluetooth discovery ACTION_FOUND
            String action = intent.getAction();
            if (action.equals("Pending Off")) {
//                Log.e(TAG, "Pending power off executed.");
                stopService();
            } else if (action.equals("Pending Request Server")) {
                requestServer();

                // Why do you schedule next request twice?
                scheduleNextRequest();
            }
        }
    };

    public static void addNewCard(Card c) {

        // If user is not already saved or is not ignored, put them into the received cards tab.
        if( !savedUnameCardPairs.containsKey( c.getUname() ) && !ignoredUNameCardPairs.containsKey( c.getUname() ) ) {
            uNameCardPairs.put(c.getUname(), c);
            removeStar( c );
            updateRecyclerView();
        }
    }

    /** Remove card from uNameCardPairs
     *
     * @param c is card to remove
     * @author Alvin Truong
     * @date 6/27/2016
     */
    public static void removeReceivedCard( Card c ) {
        if( uNameCardPairs.containsKey( c.getUname() ) ) {
            uNameCardPairs.remove(c.getUname());
            updateRecyclerView();
        }
    }

   /** Add card into ignoreUNameCardPairs
     *
     * @param c is a card to add
     * @author Alvin Truong
     * @date   6/28/2016
    */
    public static void addIgnoredCard( Card c ) {
        c.setmIgnored( true );
        ignoredUNameCardPairs.put( c.getUname(), c );
        updateRecyclerView();
    }

//    /** Remove card from ignored tab
//     *
//     * @param c card to remove
//     * @author Alvin Truong
//     * @date 6/28/2016
//    */
//    public static void removeIgnoredCard( Card c ) {
//        if( ignoredUNameCardPairs.containsKey( c.getUname() ) ){
//            ignoredUNameCardPairs.remove( c.getUname() );
//            updateRecyclerView();
//        }
//    }

    private void requestServer() {
        //depends on the implementation of the server
        //Log.e("GPS", "final gps value is " + currentLocation.toString());

        // currentLocation can be null sometimes causing a big problem.
        // @modified by Alvin Truong
        // @date 6/26/2016
        if( currentLocation != null ) {
            BackgroundConn bckConn = new BackgroundConn(getApplicationContext());
            String gps = Double.toString(currentLocation.getLatitude()) + "," + Double.toString(currentLocation.getLongitude());
            bckConn.execute("update_GPS_and_connect", myCard.getUname(), gps, max_dist);
//            Log.e(TAG, "Requested server, GPS is:" + gps);
            scheduleNextRequest();
        }
    }

    private static void addStar(Card c) {
        if (uNameCardPairs.containsKey(c.getUname())) {
            uNameCardPairs.get(c.getUname()).setmSaved(true);
        }
        updateRecyclerView();
    }

    private static void removeStar(Card c) {
        if (uNameCardPairs.containsKey(c.getUname())) {
            uNameCardPairs.get(c.getUname()).setmSaved(false);
        }
        updateRecyclerView();
    }

    public static void addSaved(Card c) {
        c.setmSaved(true);
        savedUnameCardPairs.put(c.getUname(), c);
        addStar(c);
        tempRemovedUNameCardPairs.remove( c.getUname() );

        // Add saved user to database
        BackgroundConn bckConn = new BackgroundConn( context );
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        bckConn.execute(BackgroundConn.SAVE_USER_STR, sp.getString("Login uname", null), c.getUname() );

        updateRecyclerView();

        // Remove card from received data structure
//        removeReceivedCard( c );
    }

    public static void removeSaved(Card c) {
        if (savedUnameCardPairs.containsKey(c.getUname())) {
            savedUnameCardPairs.remove(c.getUname());
            tempRemovedUNameCardPairs.put( c.getUname(), c );
//            removeStar(c);

            // Remove saved user from database
            BackgroundConn bckConn = new BackgroundConn( context );
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
            bckConn.execute(BackgroundConn.REMOVE_USER_STR, sp.getString("Login uname", null), c.getUname() );

            // Add card back into received data structure
//            c.setmSaved( false );
//            addNewCard( c );
            updateRecyclerView();
        }
    }

    public void startService() {
        // Location stuff
        //Log.d("SyncService", "Start service called");
        buttonOn();
        notificationOn();
        schedulePowerOff();
        serviceRunning = true;
        if (locationManager == null) {
            updateProfile();
            locationManager =
                    (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_LOW);
            String providerName = locationManager.getBestProvider(criteria, true);
            boolean netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (netEnabled || gpsEnabled) {
                try {
                    locationManager.requestLocationUpdates(providerName, MIN_TIME, MIN_DIST, locationListener);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                //Log.e("context", context.toString());
                Toast.makeText(context, context.getResources().getString(R.string.location_disabled), Toast.LENGTH_LONG).show();
            }
            if (currentLocation == null) {
                //Log.e("context", context.toString());
                Toast.makeText(context, context.getResources().getString(R.string.location_updating), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateProfile() {
        BackgroundConn bckConn = new BackgroundConn(getApplicationContext());
        // Register process needs modification
        bckConn.execute("update_profile", myCard.getmName(), myCard.getmPhone(), myCard.getmEmail(), myCard.getmGender(),
                myCard.getmFacebook(), myCard.getmInstagram(),
//                myCard.getmWebsite(), myCard.getmOther(), myCard.getmPhotoEncoded(), myCard.getUname());
                myCard.getmOther(), myCard.getmPhotoEncoded(), myCard.getUname());
    }

    public void stopService() {
        buttonOff();
        notificationOff();
        serviceRunning = false;

        // I do not understand why we are cancelling the pending. Don't we still need it for later?
        // @modified by Alvin Truong
        // @date 6/26/2016
        //pendingOff.cancel();

        alarmManager.cancel(pendingOff);
        alarmManager.cancel(pendingRequestServer);

        // Was getting null object reference problem.
        if( locationManager != null )
            locationManager.removeUpdates(locationListener);

        // Allows resyncing of database with update gps after first time clicking it.
        // @author Alvin Truong
        locationManager = null;

        // Upon stopping service send a coordinate that is considered "null"
        // @author Alvin Truong
        BackgroundConn bc = new BackgroundConn( getApplicationContext() );
        String gps = NULL_LATITUDE + "," + NULL_LONGITUDE;
        bc.execute("update_GPS", myCard.getUname(), gps);
    }

    private void buttonOn() {
//        menuItem.setIcon(R.drawable.onoff_green);
        menuItem.setIcon(R.drawable.ren_orange);
    }

    private void buttonOff() {
        if (menuItem != null)
//            menuItem.setIcon(R.drawable.onoff_gray);
            menuItem.setIcon(R.drawable.ren_white);
    }

    private void notificationOn() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ren_white)
                        .setColor( getResources().getColor( R.color.primaryColor ) )
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.currently_exchanging))
                        .setOngoing(true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION, mBuilder.build());
    }

    private void notificationOff() {
        if (mNotificationManager != null)
            mNotificationManager.cancel(NOTIFICATION);
    }

    public void onDestroy() {
        super.onDestroy();
        if(MainActivity.DEBUG) { Log.e(TAG, "OnDestroy()"); }
        unregisterReceiver(mReceiver);
        if(serviceRunning)
            stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if(MainActivity.DEBUG) { Log.e(TAG, "OnTaskRemoved()"); }
    }

    @Override
    public IBinder onBind(Intent intent) {
        initReceiver();
        pendingOff = PendingIntent.getBroadcast(this, 0, new Intent("Pending Off"), 0);

        // Fixed the problem with "Pending Request Server" pending intent overriding "Pending Off" intent.
        // @modified by Alvin Truong
        // @date 6/26/2016
//        pendingRequestServer = PendingIntent.getBroadcast(this, 0, new Intent("Pending Request Server"), 0);
        pendingRequestServer = PendingIntent.getBroadcast(this, 1, new Intent("Pending Request Server"), 0);
        alarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        return myBinder;
    }

    private void initReceiver() {
        // Register the BroadcastReceiver, it is used to listen "news" from our Bluetooth adapter
        IntentFilter filter = new IntentFilter();
        filter.addAction("Pending Off");
        filter.addAction("Pending Request Server");
        this.registerReceiver(mReceiver, filter);
    }

    private void scheduleNextRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + REQUESTINTERVAL, pendingRequestServer);
        } else {
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + REQUESTINTERVAL, pendingRequestServer);
        }
//        Log.e(TAG, "The next request scheduled.");
    }

    // Sets an alarm to turn off the GPS update in BackgroundConn. Currently we leave it on for 10 mins.
    private void schedulePowerOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + TEN_SECS, pendingOff);
            alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + TEN_MINS, pendingOff);
        } else {
//            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + TEN_SECS, pendingOff);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + TEN_MINS, pendingOff);
        }
//        Log.e( TAG, "Scheduled to power off in " + Integer.toString(TEN_SECS) );
    }

    // Subclass LocalBinder, returning a reference to the current instance of the BoundService class
    public class LocalBinder extends Binder {
        SyncService getService() {
            // Client call this to control service
            return SyncService.this;
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (isBetterLocation(location, currentLocation)) {
                currentLocation = location;
                if (serviceRunning) {
                    //updateProfile();
                    requestServer();

                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    };

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private static void updateRecyclerView() {
        List<Card> savedCards = new ArrayList<>(savedUnameCardPairs.values());
        TabFragment.savedCardAdapter.setCardList(savedCards);
        List<Card> receivedCards = new ArrayList<>(uNameCardPairs.values());
        TabFragment.newReceivedCardAdapter.setCardList(receivedCards);

//        TabFragment.ignoredCardAdapter.setCardList( getIgnoredCards() );
    }
}