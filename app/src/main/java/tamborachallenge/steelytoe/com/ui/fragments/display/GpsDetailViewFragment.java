package tamborachallenge.steelytoe.com.ui.fragments.display;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.NetworkUtil;
import tamborachallenge.steelytoe.com.common.events.RunningLocationService;
import tamborachallenge.steelytoe.com.common.events.ServiceBackground;
import tamborachallenge.steelytoe.com.common.events.ServiceSendSms;
import tamborachallenge.steelytoe.com.common.events.ServiceSmsFailed;
import tamborachallenge.steelytoe.com.ui.activity.ViewTrackActivity;


public class GpsDetailViewFragment extends Fragment {
    private View rootView;
    private ActionProcessButton btnActionProcess , btnActionView;
    private PendingIntent pendingIntentLocation, pendingIntentSms;
    private AlarmManager managerLocation, managerSms;

    public static GpsDetailViewFragment newInstance(){
        return new GpsDetailViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_gps_detail_view, container, false);

        btnActionProcess = (ActionProcessButton) rootView.findViewById(R.id.btnActionProcess);
        btnActionView = (ActionProcessButton) rootView.findViewById(R.id.btnActionView);

        btnActionProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadService();
                startRunningService();
                createNotification();
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

    private void startRunningService() {
        Intent intent = new Intent(getContext(), RunningLocationService.class);
        getActivity().startService(intent);
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

//                cancelAlarmServiceBackground();
//                Toast.makeText( getActivity(), "1", Toast.LENGTH_SHORT).show();


                Intent service = new Intent(getActivity(), ServiceBackground.class);
                getActivity().stopService(service);

                Intent serviceSmsSend = new Intent(getActivity(), ServiceSendSms.class);
                getActivity().stopService(serviceSmsSend);
//
//                Intent serviceSmsFailed = new Intent(getActivity(), ServiceSmsFailed.class);
//                getActivity().stopService(serviceSmsFailed);

            } else if (checkSession() == 0) {
                // Proses Start
                editor.putString("session", "1");
                editor.commit();
                setActionButtonStop__();

//                startAlarmServiceBackground();
//                startAlarmSendSms();

//                Toast.makeText( getActivity(), "0", Toast.LENGTH_SHORT).show();


                Intent service = new Intent(getActivity(), ServiceBackground.class);
                getActivity().startService(service);

                Intent serviceSmsSend = new Intent(getActivity(), ServiceSendSms.class);
                getActivity().startService(serviceSmsSend);

//                Intent serviceSmsFailed = new Intent(getActivity(), ServiceSmsFailed.class);
//                getActivity().startService(serviceSmsFailed);
            }
        }
    }

    // Button
    private void setActionButtonStart__(){
        btnActionProcess.setText(R.string.btn_start_logging);
//        actionButton.setBackgroundColor( ContextCompat.getColor(getActivity(), R.color.accentColor));
        btnActionProcess.setAlpha(0.8f);
        btnActionProcess.setProgress(0);
    }

    private void setActionButtonStop__(){
        btnActionProcess.setText(R.string.btn_stop_logging);
//        actionButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accentColorComplementary));
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

    public void cancelAlarmServiceBackground() {
//        if (managerLocation != null ) { // && managerSms != null

            managerLocation.cancel(pendingIntentLocation);

            Intent service = new Intent(getActivity(), ServiceBackground.class);
            getActivity().stopService(service);

//            managerSms.cancel(pendingIntentSms);
//            Intent serviceSms = new Intent(getActivity(), ServiceSendSms.class);
//            getActivity().stopService(serviceSms);

            Toast.makeText(getActivity(), "Get Locataion Stop", Toast.LENGTH_SHORT).show();
//        }
    }
    //======= Alarm Get Locataion END


    //======= Alarm Send Sms
//    public void startAlarmSendSms() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String intervalString =  sharedPreferences.getString("time_before_sms", null); // getting String
//        int interval;
//        if(intervalString == null || intervalString == ""){
//            editor.putString("time_before_sms", "5");
//            editor.commit();
//            interval = 5 * 60 * 1000;
//        }else{
//            interval = Integer.parseInt(intervalString) * 60 * 1000;
//        }
//
//        Toast.makeText(getActivity(), "Send Sms Start", Toast.LENGTH_SHORT).show();
//
//        Intent alarmIntentSms = new Intent(getActivity().getApplicationContext(), ServiceSendSms.class);
//        pendingIntentSms = PendingIntent.getService(getActivity(), 98, alarmIntentSms, 0);
//        managerSms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//
//        managerSms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntentSms);
//    }

    //======= Alarm Send Sms END



}
