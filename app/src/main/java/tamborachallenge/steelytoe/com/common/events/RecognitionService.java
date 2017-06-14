package tamborachallenge.steelytoe.com.common.events;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

/**
 * Created by fadlymunandar on 6/13/17.
 */

public class RecognitionService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ServiceConnection{


    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CODE_RECOGNITION = 83;
    private PendingIntent pi;

    private ServiceBackground.LocalBinder localBinder;
    private ServiceBackground serviceBackground;
    private boolean mBound;
    private BroadcastReceiver mRecogReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RecognitionReciverService.ACTION_RECOGNITION_FILTER_RECEIVER);

        mRecogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int flag = intent.getIntExtra(RecognitionReciverService.EXTRA_LOCATION_SERVICE, -1);
                handleFlag(flag);
            }
        };
        registerReceiver(mRecogReceiver, filter);

        bindLocationService(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mGoogleApiClient == null) {
            initGoogleApiClient();
        }

        mGoogleApiClient.connect();

        return START_STICKY;
    }

    private void handleFlag(int flag) {
        if (!mBound) {
            return;
        }
        switch (flag) {
            case 0:
                serviceBackground.stopLocationService();
                break;
            case 1:
                serviceBackground.startLocationService();
                break;
        }
    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(RecognitionReciverService.RECOGNITION_RECEIVER_ACTION);
        pi = PendingIntent.getService(this, REQUEST_CODE_RECOGNITION, intent, 0);

        int interval = 1 * 10 * 1000;

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, interval, pi);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void bindLocationService(boolean isWannaBind) {
        Intent intent = new Intent(this, ServiceBackground.class);

        if (isWannaBind) {
            bindService(intent,this, Context.BIND_AUTO_CREATE);
        }else {
            unbindService(this);
        }
    }



    @Override
    public void onDestroy() {
        unregisterReceiver(mRecogReceiver);
        bindLocationService(false);
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, pi);
        super.onDestroy();
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBound = true;
        localBinder = (ServiceBackground.LocalBinder) service;
        serviceBackground = localBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBound = false;
    }
}
