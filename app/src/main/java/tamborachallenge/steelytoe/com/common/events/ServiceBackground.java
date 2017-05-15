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
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import tamborachallenge.steelytoe.com.common.Maths;
import tamborachallenge.steelytoe.com.common.Strings;
import tamborachallenge.steelytoe.com.model.TempLoaction;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempLocationImpl;
import tamborachallenge.steelytoe.com.loggers.FileLoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class ServiceBackground extends Service  {

//// ***************************************************************************************************************/
//// ****************************************** LOGIKA PENCARIAN TITIK LOCATION 1 **********************************/
//// ***************************************************************************************************************/
    private static final String TAG = ServiceBackground.class.getSimpleName();

    public static final String BROADCAST_ACTION = "Hello World";
    private static final int INTERVAL = 1000 * 60 * 1;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    Context context;
    Intent intent;

    private boolean addNewTrackSegment;
    String dateTimeString;


    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        context=this;
        // Retrieve a PendingIntent that will perform a broadcast
//        Intent alarmIntent = new Intent(this, ServiceSendSms.class);
//        pendingIntent = PendingIntent.getBroadcast(this, 88, alarmIntent, 0);
//        startAlarm();

    }

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

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            // Lokasi baru selalu lebih baik daripada tidak ada lokasi
            return true;
        }

        // Check whether the new location fix is newer or older
        // Periksa apakah lokasi baru fix lebih baru atau lebih tua
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -INTERVAL;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        // Jika sudah lebih dari dua menit karena lokasi saat ini, menggunakan lokasi baru
        // Karena pengguna memiliki kemungkinan pindah
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
            // Jika lokasi baru yang lebih dari dua menit lebih tua, itu harus lebih baru
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        // Memerikas apakah lokasi baru memperbaiki lebih atau kurang akurat
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        // Periksa apakah lokasi lama dan baru dari provider yang sama
        boolean isFromSameProvider = isSameProvider(location.getProvider(),currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        // Menentukan kualitas lokasi menggunakan kombinasi ketepatan waktu dan akurasi
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        Log.v("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);

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


    //======================================================================================================== Alarm
    public void startAlarm() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String intervalString =  sharedPreferences.getString("time_before_sms", null); // getting String
        int interval;
        if(intervalString == null || intervalString == ""){
            editor.putString("time_before_sms", "5");
            editor.commit();
            interval = 5 * 60 * 1000;
        }else{
            interval = Integer.parseInt(intervalString) * 60 * 1000;
        }

        Intent alarmIntent = new Intent(this, ServiceSendSms.class);
        pendingIntent = PendingIntent.getBroadcast(this, 77, alarmIntent, 0);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    public void cancelAlarm() {
        if (manager != null) {
            manager.cancel(pendingIntent);
            Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }



}
