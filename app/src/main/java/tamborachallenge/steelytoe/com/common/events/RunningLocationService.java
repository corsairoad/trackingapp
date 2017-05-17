package tamborachallenge.steelytoe.com.common.events;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import tamborachallenge.steelytoe.com.MainActivity;
import tamborachallenge.steelytoe.com.R;

/**
 * Created by fadlymunandar on 5/15/17.
 */

public class RunningLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String SERVICE_NAME = RunningLocationService.class.getSimpleName();
    private static final int ONGOING_NOTIFICATION_ID = 4;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;

    public RunningLocationService() {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d("Location update", "Onstart Command called");
        //return super.onStartCommand(intent, flags, startId);
        mGoogleApiClient.connect();
        return Service.START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        this.mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        createLocationRequest();
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        publishLocation();
    }

    private void publishLocation(){
        String lat = String.valueOf(mLocation.getLatitude());
        String lng = String.valueOf(mLocation.getLongitude());
        Log.d("Location Updates", "Lat: " + lat + " Lng " + lng);
        updateNotification();
    }

    private void updateNotification(){
        String lat = String.valueOf(mLocation.getLatitude());
        String lng = String.valueOf(mLocation.getLongitude());

        NotificationManager notifManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,2, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Aplikasi Lari")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Lat: " + lat + " Lng " + lng)
                //.addAction(R.mipmap.ic_launcher, "Open", pendingIntent)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        //notifManager.notify(0,mBuilder.build());
        startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());
    }

    // end
}

