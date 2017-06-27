package tamborachallenge.steelytoe.com.ui.fragments.display;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.events.RecognitionReciverService;
import tamborachallenge.steelytoe.com.common.events.RecognitionService;
import tamborachallenge.steelytoe.com.common.events.ServiceBackground;
import tamborachallenge.steelytoe.com.ui.activity.ViewTrackActivity;
import tamborachallenge.steelytoe.com.util.PrefManager;


public class GpsDetailViewFragment extends Fragment{

    private static final String TAG = GpsDetailViewFragment.class.getSimpleName();

    private View rootView;
    private ActionProcessButton btnActionProcess , btnActionView;
    private PendingIntent pendingIntentLocation, pendingIntentSms;
    private AlarmManager managerLocation, managerSms;
    private GoogleApiClient mGoogleApiClient;

    private PrefManager prefManager;
    private int hour;
    private int minute;
    private int second;
    private String timerString;
    private BroadcastReceiver timerReceiver;
    private TextView textTimer;
    private TextView textSpeed;
    private TextView textDistance;
    private TextView textActivity;
    private TextView textStartedTime;
    private LinearLayout containerStartedTime;

    private static final int REQUEST_PERMISSION_REQUEST_CODE = 2;


    public static GpsDetailViewFragment newInstance(){
        return new GpsDetailViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = PrefManager.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gps_detail_view, container, false);

        textSpeed = (TextView) rootView.findViewById(R.id.text_speed);
        textDistance = (TextView) rootView.findViewById(R.id.text_distance);
        btnActionProcess = (ActionProcessButton) rootView.findViewById(R.id.btnActionProcess);
        textActivity = (TextView) rootView.findViewById(R.id.text_activity);
        btnActionView = (ActionProcessButton) rootView.findViewById(R.id.btnActionView);
        textTimer = (TextView) rootView.findViewById(R.id.text_timer);
        textTimer.setText(timerString);
        textStartedTime = (TextView) rootView.findViewById(R.id.text_started_time);
        containerStartedTime = (LinearLayout) rootView.findViewById(R.id.container_started_time);
        containerStartedTime.setVisibility(View.GONE);

        btnActionProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()){
                    requestPermission();
                } else {
                    loadService(); // location service
                }
                //mGoogleApiClient.connect();
            }
        });

        btnActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ViewTrackActivity.class));
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        createAndRegisterReceiver();
        initTimer();
        setStartedTime();
    }

    @Override
    public void onResume() {
        if(checkSession()  == 0){
            setActionButtonStart__();
        } else {
            setActionButtonStop__();
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(timerReceiver);
        super.onStop();
    }

    @Override
    public void onPause()    {
        super.onPause();
    }

    private void createNotification() {
        NotificationManager notif = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =  new NotificationCompat.Builder(getContext())
                .setContentTitle("Aplikasi Lari")
                .setContentText("Service running")
                .setSmallIcon(R.mipmap.ic_launcher);

        notif.notify(0, builder.build());
    }

    // =====================================================================
    public void loadService(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent service = new Intent(getContext(), ServiceBackground.class);
        Intent recSercvice = new Intent(getContext(), RecognitionService.class);

        PrefManager prefManager = PrefManager.getInstance(getContext());

        if(checkGps() == 0 ) {
            Toast.makeText( getActivity(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        } else {
            if (checkSession() == 1) {
                // Proses Stop
                editor.putString("session", "0");
                editor.commit();
                setActionButtonStart__();

                getActivity().stopService(service);
                getActivity().stopService(recSercvice);

                prefManager.setLastDistance(0); // reset last distance
                prefManager.addStillFlagActivity(0);
                prefManager.setStartedTime(null); // reset started time when user pressed stop running button

            } else if (checkSession() == 0) {
                // Proses Start
                editor.putString("session", "1");
                editor.commit();
                setActionButtonStop__();

                getActivity().startService(service);
                getActivity().startService(recSercvice);
                setStartedTime();

            }
        }
    }

    private void setStartedTime() {
        String time = prefManager.getStartedTime();

        if (time == null) {
            if (checkSession() == 0) {
                containerStartedTime.setVisibility(View.GONE);
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            time = sdf.format(new Date());
            prefManager.setStartedTime(time);
        }

        textStartedTime.setText(time);
        containerStartedTime.setVisibility(View.VISIBLE);
    }

    // Button
    private void setActionButtonStart__(){
        btnActionProcess.setText(R.string.btn_start_logging);
        btnActionProcess.setAlpha(0.8f);
        btnActionProcess.setProgress(0);
    }

    private void setActionButtonStop__(){
        btnActionProcess.setText(R.string.btn_stop_logging);
        btnActionProcess.setAlpha(0.8f);
        btnActionProcess.setProgress(50);
    }


    //check Status Session
    private final int checkSession(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String session = sharedPreferences.getString("session", null);
        int stsSession = 0;
        if(session == null || TextUtils.isEmpty(session)){
            editor.putString("session", "0");
            editor.commit();
        }else{
            stsSession = Integer.parseInt(session);
        }
        return stsSession;
    }

    /*Check GPS*/
    private final int checkGps(){
        ContentResolver contentResolver = getActivity().getContentResolver();
        int mode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
        return mode;
    }


    //======= Alarm Get Locataion
    public void startAlarmServiceBackground() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String intervalString =  sharedPreferences.getString("time_before_logging", null); // getting String
        long interval;
        if(intervalString == null || intervalString == ""){
            editor.putString("time_before_logging", "15");
            editor.commit();
            interval = 15 * 1000;
        }else{
            interval = Long.parseLong(intervalString) * 1000;
        }
        Toast.makeText(getActivity(), "Get Locataion Start", Toast.LENGTH_SHORT).show();

        Intent alarmLocation = new Intent(getActivity().getApplicationContext(), ServiceBackground.class);
        pendingIntentLocation = PendingIntent.getService(getActivity(), 99, alarmLocation, 0);
        managerLocation = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        managerLocation.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntentLocation);
    }

    private boolean checkPermission() {
        int locationPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int sendSmsPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
        int readSmsPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
        int receiveSmsPermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS);
        int readPhoneStatePermission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE);


        if (locationPermission == PackageManager.PERMISSION_GRANTED
                && cameraPermission == PackageManager.PERMISSION_GRANTED
                && sendSmsPermission == PackageManager.PERMISSION_GRANTED
                && readSmsPermission == PackageManager.PERMISSION_GRANTED
                && receiveSmsPermission == PackageManager.PERMISSION_GRANTED
                && readPhoneStatePermission == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;

    }

    private void requestPermission() {
        boolean shouldShowPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        final String[] permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE};

        if (shouldShowPermissionRationale) {
            Snackbar.make(getActivity().findViewById(R.id.content_main), getString(R.string.permission_rationale),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OKE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(getActivity(), permissions,
                                    REQUEST_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .show();

        }else {
            ActivityCompat.requestPermissions(getActivity(), permissions,
                    REQUEST_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int grantedResult = 0;
        switch (requestCode) {
            case REQUEST_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    int grantSize = grantResults.length;
                    for (int i=0; i<grantSize; i++) {
                        if (grantResults[i]== PackageManager.PERMISSION_GRANTED) {
                            grantedResult+=1;
                        }
                    }

                    if (grantedResult == grantSize) {
                        loadService();
                    }else {
                        Snackbar.make(getActivity().findViewById(R.id.content_main), getString(R.string.permission_rationale),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction("OKE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermission();
                                    }
                                })
                                .show();                    }
                }

        }
    }

    private void setActMovement(String act) {
        textActivity.setText(act);
    }

    private void setSpeedAndDistance(String speed, String distance) {
        textSpeed.setText(speed);
        textDistance.setText(distance);
    }


    private void initTimer() {
        hour = prefManager.getHour();
        minute = prefManager.getMinute();
        second = prefManager.getSecond();

        timerString = String.format("%2d:%02d:%02d", hour, minute, second);
        textTimer.setText(timerString);
    }

    private void createAndRegisterReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ServiceBackground.ACTION_TIMER_RECIVER);
        filter.addAction(ServiceBackground.ACTION_BIND_SERVICE);
        filter.addAction(ServiceBackground.ACTION_SPEED_DISTANCE_RECEIVER);
        filter.addAction(RecognitionReciverService.ACTION_ACTIVITY_MOVEMENT);

        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ServiceBackground.ACTION_TIMER_RECIVER:
                        initTimer();
                        break;
                    case ServiceBackground.ACTION_BIND_SERVICE:
                        //int flag = intent.getIntExtra(ServiceBackground.EXTRA_BIND_SERVICE, -1);
                        //doSomeThingWithLocationService(flag);
                        break;
                    case ServiceBackground.ACTION_SPEED_DISTANCE_RECEIVER:
                        String speed = intent.getStringExtra(ServiceBackground.EXTRA_SPEED);
                        String distance = intent.getStringExtra(ServiceBackground.EXTRA_DISTANCE);
                        setSpeedAndDistance(speed, distance);
                        break;
                    case RecognitionReciverService.ACTION_ACTIVITY_MOVEMENT:
                        String act = intent.getStringExtra(RecognitionReciverService.EXTRA_ACTIVITY_MOVEMENT);
                        setActMovement(act);
                }
            }
        };

        getActivity().registerReceiver(timerReceiver, filter);
    }


}
