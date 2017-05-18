package tamborachallenge.steelytoe.com.common.events;

import android.Manifest;
import android.app.IntentService;
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
import android.text.format.Time;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import tamborachallenge.steelytoe.com.MainActivity;
import tamborachallenge.steelytoe.com.R;

/**
 * Created by fadlymunandar on 5/15/17.
 */

public class RunningLocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String SERVICE_NAME = RunningLocationService.class.getSimpleName();
    private Intent mIntentLocationService;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;


    public RunningLocationService() {
        super(SERVICE_NAME);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mIntentLocationService = new Intent(this, ServiceBackground.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        if (result != null) {

            List<DetectedActivity> detectedActivities = result.getProbableActivities();
            for (DetectedActivity detectedActivity : detectedActivities){
                switch (detectedActivity.getType()) {
                    case DetectedActivity.RUNNING:
                        case DetectedActivity.IN_VEHICLE:
                            case DetectedActivity.ON_BICYCLE:
                                case DetectedActivity.WALKING:
                                    int confidence = detectedActivity.getConfidence();
                                    if (confidence >= 75) {
                                        startService(mIntentLocationService);
                                    }
                }
            }
        }
    }

    private Location getLocation(GoogleApiClient googleApiClient) {
        if (googleApiClient != null) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        return null;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

