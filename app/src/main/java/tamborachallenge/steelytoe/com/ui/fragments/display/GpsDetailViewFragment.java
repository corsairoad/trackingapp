package tamborachallenge.steelytoe.com.ui.fragments.display;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;

import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.NetworkUtil;
import tamborachallenge.steelytoe.com.common.events.RunningLocationService;
import tamborachallenge.steelytoe.com.common.events.ServiceBackground;
import tamborachallenge.steelytoe.com.common.events.ServiceSendSms;
import tamborachallenge.steelytoe.com.common.events.ServiceSmsFailed;
import tamborachallenge.steelytoe.com.ui.activity.ViewTrackActivity;
import tamborachallenge.steelytoe.com.util.PrefManager;


public class GpsDetailViewFragment extends Fragment {
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


    public static GpsDetailViewFragment newInstance(){
        return new GpsDetailViewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = PrefManager.getInstance(getContext());
        createAndRegisterReceiver();
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

        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ServiceBackground.ACTION_TIMER_RECIVER.equals(intent.getAction())){
                    initTimer();
                }
            }
        };

        getActivity().registerReceiver(timerReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gps_detail_view, container, false);

        btnActionProcess = (ActionProcessButton) rootView.findViewById(R.id.btnActionProcess);
        btnActionView = (ActionProcessButton) rootView.findViewById(R.id.btnActionView);
        textTimer = (TextView) rootView.findViewById(R.id.text_timer);
        textTimer.setText(timerString);
        btnActionProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadService();
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
        initTimer();
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
        if(checkGps() == 0 ) {
            Toast.makeText( getActivity(), "Gps Disable", Toast.LENGTH_SHORT).show();
        } else {
            if (checkSession() == 1) {
                // Proses Stop
                editor.putString("session", "0");
                editor.commit();
                setActionButtonStart__();

                Intent service = new Intent(getActivity(), ServiceBackground.class);
                getActivity().stopService(service);

                //Intent serviceSmsSend = new Intent(getActivity(), ServiceSendSms.class);
                //getActivity().stopService(serviceSmsSend);


            } else if (checkSession() == 0) {
                // Proses Start
                editor.putString("session", "1");
                editor.commit();
                setActionButtonStop__();

                Intent service = new Intent(getActivity(), ServiceBackground.class);
                getActivity().startService(service);

                //Intent serviceSmsSend = new Intent(getActivity(), ServiceSendSms.class);
                //getActivity().startService(serviceSmsSend);

            }
        }
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









}
