package tamborachallenge.steelytoe.com.common.events;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

import tamborachallenge.steelytoe.com.MainActivity;
import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempLocationImpl;
import tamborachallenge.steelytoe.com.common.Maths;
import tamborachallenge.steelytoe.com.common.Strings;
import tamborachallenge.steelytoe.com.loggers.FileLoggerFactory;
import tamborachallenge.steelytoe.com.model.TempLoaction;
import tamborachallenge.steelytoe.com.util.PrefManager;

public class ServiceBackground extends Service implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private static final String TAG = ServiceBackground.class.getSimpleName();

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int INTERVAL = 1000 * 60 * 1;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;

    Context context;
    Intent intent;

    //-- start
    public static final String ACTION_TIMER_RECIVER = "timer.receiver";
    public static final String ACTION_BIND_SERVICE = "com.bind.service";
    public static final String ACTION_SPEED_DISTANCE_RECEIVER = "com.speed.distance";
    public static final String EXTRA_SPEED = "com.extra.speed";
    public static final String EXTRA_DISTANCE = "com.extra.distance";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Location firstLocation;
    private long mInterval;
    private static final int NOTIFICATION_ONGOING_ID = 32;
    public static final int REQUEST_CODE_MAIN = 76;

    private int timerInterval = 1000; //in millis
    private Handler timerHandler;
    private Runnable timerRunnable;

    private int minute = 0;
    private int second = 0;
    private int hour = 0;
    private String timerString;

    private NotificationCompat.Builder notifBuilder;
    private final IBinder mBinder = new LocalBinder();
    // -- end

    private boolean addNewTrackSegment;
    String dateTimeString;


    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        context = this;

        initGoogleApiClient();


        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (second == 59) {
                    second = 0;
                    minute += 1;
                } else {
                    second += 1;
                }

                if (minute == 60) {
                    minute = 0;
                    hour += 1;
                }

                timerString = String.format("%02d:%02d:%02d", hour, minute, second);
                updateTimerReceiver(hour, minute, second);
                updateTimerNotification(timerString);
                timerHandler.postDelayed(this, timerInterval);
            }
        };


    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void updateTimerReceiver(int hour, int minute, int second) {
        PrefManager prefManager = PrefManager.getInstance(this);
        prefManager.saveHour(hour);
        prefManager.saveMinute(minute);
        prefManager.saveSecond(second);

        Intent intent = new Intent();
        intent.setAction(ACTION_TIMER_RECIVER);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ServiceBackground", "onStartCommand() called");

        mInterval = getInterval();
        //mInterval = 1000 * 60 * 2;
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        createLocationRequest();

        timerHandler.postDelayed(timerRunnable, timerInterval);

        return START_STICKY;

    }


    /*
    @Override
    public void onStart(Intent intent, int startId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String intervalString =  sharedPreferences.getString("time_before_logging", null); // getting String
        long interval;
        if(TextUtils.isEmpty(intervalString)){
            editor.putString("time_before_logging", "15");
            editor.commit();
            interval = 15 * 1000;
        }else{
            interval = Long.parseLong(intervalString) * 1000;
        }
        Log.d(TAG, "Interval " + interval);

        // do something
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, interval, 0,listener);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, interval, 0,listener);

    }
    */

    // Google API Client methods
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(mInterval / 2);
        mLocationRequest.setInterval(mInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    // location update listener
    @Override
    public void onLocationChanged(Location location) {
        this.mCurrentLocation = location;
        setBestLocation(this.mCurrentLocation);
        //updateNotification(location);
    }

    private void updateTimerNotification(String timerString) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE_MAIN, intent, 0);
        if (notifBuilder == null){
           notifBuilder = new NotificationCompat.Builder(this);

        }
        notifBuilder
                .setContentTitle("Durasi")
                .setContentText(timerString)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .addAction(0, "OPEN", pendingIntent);

        startForeground(NOTIFICATION_ONGOING_ID, notifBuilder.build());
        Log.d("Location update", timerString);
    }

    private void updateNotification(Location location){
        if (location == null){
            return;
        }
        String provider = location.getProvider();
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        String contentText = String.format("Lat: %s Lng: %s\nProvider: %s", lat, lng, provider);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Aplikasi Lari")
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true);

        startForeground(NOTIFICATION_ONGOING_ID, notifBuilder.build());
        Log.d("Location update", contentText);
    }

    private long getInterval(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String intervalString =  sharedPreferences.getString("time_before_logging", null); // getting String
        long interval;

        if(TextUtils.isEmpty(intervalString)){
            editor.putString("time_before_logging", "15");
            editor.apply();
            interval = 1 * 10 * 1000;
        }else{
            interval = Long.parseLong(intervalString) * 1000;
        }

        Log.d(TAG, "Interval " + interval);
        return interval;
    }

    private void setBestLocation(Location loc){
        if (loc == null) {
            return;
        }
            if(isLocationOke(previousBestLocation, loc)) {
                Log.d("Current Location", " " + loc);
                Log.d("Previous Location", " " + previousBestLocation);

                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());

                sendBroadcast(intent);

                sendSpeedAndDistanceBroadcast(loc);

                Time now = new Time();
                now.setToNow();
                String timeOfEvent = now.format("%H:%M:%S");

                insertToSQLiteDatabase(loc, timeOfEvent);
                insertToGpxFile(loc, timeOfEvent);
                writeToFile(loc);
            }
            previousBestLocation = loc;
    }

    private void sendSpeedAndDistanceBroadcast(Location currentLocation) {
        DecimalFormat decimalFormat = new DecimalFormat("####.##");
        PrefManager prefManager = PrefManager.getInstance(this);

        float newDistance = (float) ((previousBestLocation.distanceTo(currentLocation)) * 0.001);
        float lastDistance = prefManager.getLastDistance();
        float totalDistance = lastDistance + newDistance;
        prefManager.setLastDistance(totalDistance);

        String speed = decimalFormat.format((currentLocation.getSpeed()*3600)/1000);
        String distance = decimalFormat.format(totalDistance);

        Intent intent = new Intent(ACTION_SPEED_DISTANCE_RECEIVER);
        intent.putExtra(EXTRA_SPEED, speed);
        intent.putExtra(EXTRA_DISTANCE, distance);

        sendBroadcast(intent);
    }


    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            if(isBetterLocation(loc, previousBestLocation)) {
                Log.d("__A"," " + loc);
                Log.d("__B"," " + previousBestLocation);

                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                Time now = new Time();
                now.setToNow();
                String timeOfEvent = now.format("%H:%M:%S");

                insertToSQLiteDatabase(loc, timeOfEvent);
                insertToGpxFile(loc, timeOfEvent);
                writeToFile(loc);
            }

            previousBestLocation = loc;

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

    /*
    / Location filter algorithms
    /
     */

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -INTERVAL;
        boolean isNewer = timeDelta > 0;
        double locSpeed = location.getSpeed();

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
        boolean isMoreAccurate = accuracyDelta < 50;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        }
        return false;
    }

    private boolean isLocationOke(Location oldLocation, Location newLocation) {
        if(oldLocation == null) {
            /*
            firstLocation = new Location("first_location");
            firstLocation.setLatitude(newLocation.getLatitude());
            firstLocation.setLongitude(newLocation.getLongitude());
            */
            previousBestLocation = newLocation;
            return true;
        }

        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();

        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if(isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if(isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();

            // If time difference is not greater then allowed threshold we accept it.
            if(timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
                return true;
            }
        }

        return false;

    }

    private boolean isLocationApproved(Location currentLocation, Location previousLocation){
        if (previousLocation == null) {
            return false;
        }

        double distanceBetween = currentLocation.distanceTo(previousLocation);
        double currLocationSpeed = currentLocation.getSpeed();
        double currLocationAccuracy = currentLocation.getAccuracy();
        boolean isMoreAccurate =  currLocationAccuracy <= 40.0;
        boolean isSpeedApproved = currLocationSpeed > 0.2;

        String logText = String.format("\n%s:%.2f\n%s:%.2f\n%s: %s","Location accuracy", currLocationAccuracy,"Location Speed", currLocationSpeed, "Is more accurate", isMoreAccurate);
        generateNoteOnSD(this,"log_location.txt", logText);

        Log.d("Location details", "distance: " + distanceBetween + " speed: " + currLocationSpeed + " Accuracy: " + currLocationAccuracy);
        if (isMoreAccurate && isSpeedApproved) {
            return true;
        }
        return false;
    }

    // ---- End location filter

    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");

            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);

            if (!gpxfile.exists()) {
                gpxfile.createNewFile();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(gpxfile,true));
            bufferedWriter.append(sBody);
            bufferedWriter.newLine();
            bufferedWriter.close();

            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Checks whether two providers are the same */
    /** Memerikas apakah dua Provider adalah sama */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            Log.d("F","F");
            return provider2 == null;
        }
        Log.d("G","G");
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        Log.v("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        PrefManager.getInstance(this).resetTime();
        timerHandler.removeCallbacks(timerRunnable);
        mGoogleApiClient.disconnect();
        //-- -locationManager.removeUpdates(listener);

//        cancelAlarm();
        super.onDestroy();
    }


    //Write Location
    private void writeToFile(Location loc) {
        try {
            FileLoggerFactory.write(getApplicationContext(), loc);
        }
        catch(Exception e){
            Log.d(TAG, "" + e.getMessage());
        }

    }


    // ================================================================================== Insert To Database
    private final void insertToSQLiteDatabase(Location loc, String timer){
        int _Id=0;

        CrudTempLocationImpl crudTempLocatio= new CrudTempLocationImpl(this);
        TempLoaction tempLoaction= new TempLoaction();

        tempLoaction.lat = String.valueOf(loc.getLatitude());
        tempLoaction.lng = String.valueOf(loc.getLongitude());
        tempLoaction.timer = timer;
        tempLoaction.id=_Id;

        if (_Id == 0 ){
            _Id = crudTempLocatio.insert(tempLoaction);
            Toast.makeText(this,"Location " + _Id , Toast.LENGTH_SHORT).show();
        }


    }

    //======================================================================================================== Insert To File GPX
    private final void insertToGpxFile(Location loc, String timer){
        try {
            // Check Folder ~files
            File myDir = new File(getFilesDir().getAbsolutePath());
            if (!myDir.exists()){
                myDir.mkdir();
            }

            //Check File ~log.gpx di folder ~files
            File file = new File(myDir+"/log.gpx");
            if (!file.exists()){
                FileOutputStream fOut = openFileOutput("log.gpx", Context.MODE_PRIVATE);
                fOut.write(getBeginningXml(dateTimeString).getBytes());
                fOut.write("<trk>".getBytes());
                fOut.write(getEndXml().getBytes());
                fOut.flush();
                fOut.close();
                addNewTrackSegment = true;

                if(loc!=null){
                    long length = new File(getFilesDir().getAbsolutePath() + "/log.gpx").length();

                    int offsetFromEnd = (addNewTrackSegment) ? getEndXml().length() : getEndXmlWithSegment().length();
                    long startPosition = length - offsetFromEnd;
                    String trackPoint = getTrackPointXml(loc, timer);

                    RandomAccessFile raf = new RandomAccessFile(myDir + "/log.gpx", "rw");
                    raf.seek(startPosition);
                    raf.write(trackPoint.getBytes());
                    raf.close();

                    /*Log.d(TAG, "length " + length);
                    Log.d(TAG, "getBeginningXml " + getBeginningXml(dateTimeString).length());
                    Log.d(TAG, "getEndXml().length() " + getEndXml().length());
                    Log.d(TAG, "getEndXmlWithSegment().length() " + getEndXmlWithSegment().length());
                    Log.d(TAG, "offsetFromEnd " + offsetFromEnd);
                    Log.d(TAG, "startPosition " + startPosition);
                    Log.d(TAG, "raf " + raf);*/
                    addNewTrackSegment = false;
                }

            } else {
                long length = new File(getFilesDir().getAbsolutePath() + "/log.gpx").length();

                int offsetFromEnd = (addNewTrackSegment) ? getEndXml().length() : getEndXmlWithSegment().length();
                long startPosition = length - offsetFromEnd;
                String trackPoint = getTrackPointXml(loc, timer);

                RandomAccessFile raf = new RandomAccessFile(myDir + "/log.gpx", "rw");
                raf.seek(startPosition);
                raf.write(trackPoint.getBytes());
                raf.close();

                /*Log.d(TAG, "length " + length);
                Log.d(TAG, "getBeginningXml " + getBeginningXml(dateTimeString).length());
                Log.d(TAG, "getEndXml().length() " + getEndXml().length());
                Log.d(TAG, "getEndXmlWithSegment().length() " + getEndXmlWithSegment().length());
                Log.d(TAG, "offsetFromEnd " + offsetFromEnd);
                Log.d(TAG, "startPosition " + startPosition);
                Log.d(TAG, "raf " + raf);*/
                addNewTrackSegment = false;
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    String getEndXml(){
        return "</trk></gpx>";
    }
    String getEndXmlWithSegment(){
        return "</trkseg></trk></gpx>";
    }

    String getBeginningXml(String dateTimeString){
        StringBuilder initialXml = new StringBuilder();
        initialXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        initialXml.append("<gpx version=\"1.0\" creator=\"GPSLogger Hadi - http://gpslogger.hadi.com/\" ");
        initialXml.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        initialXml.append("xmlns=\"http://www.topografix.com/GPX/1/0\" ");
        initialXml.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 ");
        initialXml.append("http://www.topografix.com/GPX/1/0/gpx.xsd\">");
        initialXml.append("<time>").append(dateTimeString).append("</time>");
        return initialXml.toString();
    }

    String getTrackPointXml(Location loc,String timer) {

        StringBuilder track = new StringBuilder();

        if (addNewTrackSegment) {
            track.append("<trkseg>");
        }

        track.append("<trkpt lat=\"")
                .append(String.valueOf(loc.getLatitude()))
                .append("\" lon=\"")
                .append(String.valueOf(loc.getLongitude()))
                .append("\">");

        if (loc.hasAltitude()) {
            track.append("<ele>").append(String.valueOf(loc.getAltitude())).append("</ele>");
        }

        track.append("<time>").append(timer).append("</time>");

        if (loc.hasBearing()) {
            track.append("<course>").append(String.valueOf(loc.getBearing())).append("</course>");
        }

        if (loc.hasSpeed()) {
            track.append("<speed>").append(String.valueOf(loc.getSpeed())).append("</speed>");
        }

        if (loc.getExtras() != null) {
            String geoidheight = loc.getExtras().getString("GEOIDHEIGHT");

            if (!Strings.isNullOrEmpty(geoidheight)) {
                track.append("<geoidheight>").append(geoidheight).append("</geoidheight>");
            }
        }

        track.append("<src>").append(loc.getProvider()).append("</src>");


        if (loc.getExtras() != null) {

            int sat = Maths.getBundledSatelliteCount(loc);

            if(sat > 0){
                track.append("<sat>").append(String.valueOf(sat)).append("</sat>");
            }


            String hdop = loc.getExtras().getString("HDOP");
            String pdop = loc.getExtras().getString("PDOP");
            String vdop = loc.getExtras().getString("VDOP");
            String ageofdgpsdata = loc.getExtras().getString("AGEOFDGPSDATA");
            String dgpsid = loc.getExtras().getString("DGPSID");

            if (!Strings.isNullOrEmpty(hdop)) {
                track.append("<hdop>").append(hdop).append("</hdop>");
            }

            if (!Strings.isNullOrEmpty(vdop)) {
                track.append("<vdop>").append(vdop).append("</vdop>");
            }

            if (!Strings.isNullOrEmpty(pdop)) {
                track.append("<pdop>").append(pdop).append("</pdop>");
            }

            if (!Strings.isNullOrEmpty(ageofdgpsdata)) {
                track.append("<ageofdgpsdata>").append(ageofdgpsdata).append("</ageofdgpsdata>");
            }

            if (!Strings.isNullOrEmpty(dgpsid)) {
                track.append("<dgpsid>").append(dgpsid).append("</dgpsid>");
            }
        }



        track.append("</trkpt>\n");

        track.append("</trkseg></trk></gpx>");

        return track.toString();
    }

    public class LocalBinder extends Binder {

        public ServiceBackground getService() {
            return ServiceBackground.this;
        }
    }

    public void stopLocationService() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Log.d(TAG, "GOOGLE LOCATION SERVICE DISCONNECTED");
        }
    }

    public void startLocationService() {
        if (mGoogleApiClient == null) {
            initGoogleApiClient();
        }
        mGoogleApiClient.connect();
        Log.d(TAG, "RESTART GOOGLE LOCATION SERVICE");
    }


}
