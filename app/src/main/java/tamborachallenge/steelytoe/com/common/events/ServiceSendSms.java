package tamborachallenge.steelytoe.com.common.events;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tamborachallenge.steelytoe.com.common.Impl.CrudSmsLoc;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempDeliverySmsFailed;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempDeliverySmsSent;
import tamborachallenge.steelytoe.com.common.Impl.CrudTempLocationImpl;
import tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed;
import tamborachallenge.steelytoe.com.model.TempDeliverySmsSent;

import static tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed.KEY_ID;
import static tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed.KEY_SMS_INTERVAL;
import static tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed.KEY_TIME_SMS;
import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lat;
import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lng;

/**
 * Created by haiv on 25/03/17.
 */

public class ServiceSendSms extends Service {
    private static final String TAG = ServiceSendSms.class.getSimpleName();
    Handler timerHandler = new Handler();

    CrudSmsLoc crudSmsLoc = new CrudSmsLoc(this);
    CrudTempDeliverySmsFailed crudTempDeliverySmsFailed = new CrudTempDeliverySmsFailed(this);
    CrudTempDeliverySmsSent crudTempDeliverySmsSent = new CrudTempDeliverySmsSent(this);

    String numberDestination, timeOfEvent, rowCont, sts_delivery, SENT = "SMS_SENT";
    String data_sms_failed, timeOfEventFailed;

    int rowContFailed, idFailed;

    PendingIntent sentPI;
    SmsManager smsMgr = SmsManager.getDefault();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sentPI = PendingIntent.getBroadcast(this, 22, new Intent(SENT), 0);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(SENT));

        /*Check Phone Nomber*/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        numberDestination = sharedPreferences.getString("sms_server_number", null); // getting String
        if (TextUtils.isEmpty(numberDestination)) {
            numberDestination = "082211122203";
            editor.putString("sms_server_number", numberDestination);
            editor.commit();

        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        timerHandler.postDelayed(timerRunnable, 0);
        timerHandler.postDelayed(timerRunnableFailed, 0);

    }

    @Override
    public void onDestroy() {
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.removeCallbacks(timerRunnableFailed);
        unregisterReceiver(mBatInfoReceiver);
        super.onDestroy();
    }

    ///=========================================================================================================
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String intervalString =  sharedPreferences.getString("time_before_sms", null); // getting String
            int interval;
            if(intervalString == null || intervalString == ""){
                editor.putString("time_before_sms", "5");
                editor.commit();
                interval = 5 * 60 * 1000;
            } else {
                interval = Integer.parseInt(intervalString) * 60 * 1000;
            }

            /*Time*/
            Time now = new Time();
            now.setToNow();
            timeOfEvent = now.format("%H:%M:%S");
            Log.d(TAG, "timerRunnable ");
            sendSms();
            timerHandler.postDelayed(this, interval);
        }
    };



    private void sendSms() {
        rowCont = crudSmsLoc.getRowCount();
        if (rowCont != null) {
            if (isSimExists()) {
                try {
                    smsMgr.sendTextMessage(numberDestination, null, rowCont, sentPI, null);
                } catch (Exception e) {
                    sts_delivery = "Failed to send SMS";
                    insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                    e.printStackTrace();
                }
            } else {
                sts_delivery = "Cannot send SMS";
                insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
            }
        }
    }


    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int resultCode = getResultCode();
            Log.d(TAG, "resultCode " + resultCode);

            switch (resultCode) {
                case Activity.RESULT_OK:
                    sts_delivery = "Sent";
                    insertToSQLiteDatabase_SmsSent(timeOfEvent, rowCont, sts_delivery);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    sts_delivery = "Generic failure";
                    insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    sts_delivery = "No service";
                    insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    sts_delivery = "Null PDU";
                    insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    sts_delivery = "Radio off";
                    insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                    break;
            }
        }

    };

    public boolean isSimExists(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if(SIM_STATE == TelephonyManager.SIM_STATE_READY)
            return true;
        else {
            switch(SIM_STATE){
                case TelephonyManager.SIM_STATE_ABSENT: //SimState = "No Sim Found!";
                    Toast.makeText(getBaseContext(), "No Sim Found!", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = "Network Locked!";
                    Toast.makeText(getBaseContext(), "SNetwork Locked!", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = "PIN Required to access SIM!";
                    Toast.makeText(getBaseContext(), "PIN Required to access SIM!", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = "PUK Required to access SIM!"; // Personal Unblocking Code
                    Toast.makeText(getBaseContext(), "PUK Required to access SIM!", Toast.LENGTH_LONG).show();
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = "Unknown SIM State!";
                    Toast.makeText(getBaseContext(), "Unknown SIM State!", Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }
    }

    // ================================================================================== Insert To Database
    private final void insertToSQLiteDatabase_SmsFailed(String timeOfEventFailed, String sendSmsFailed, String sts_deliveryFailed){
        int _Id = 0 ;

        TempDeliverySmsFailed timTempDeliverySmsFailed = new TempDeliverySmsFailed();
        timTempDeliverySmsFailed.time_sms = timeOfEventFailed;
        timTempDeliverySmsFailed.sms_interval = sendSmsFailed;
        timTempDeliverySmsFailed.status_delivery = sts_deliveryFailed;

        timTempDeliverySmsFailed.id=_Id;
        if(sendSmsFailed != null) {
            crudTempDeliverySmsFailed.insert(timTempDeliverySmsFailed);
            rowCont = null;
            timeOfEvent = null;
            crudSmsLoc.deleteAll();
            Log.d(TAG, "Failed " + sendSmsFailed);
        }


    }

    public final void insertToSQLiteDatabase_SmsSent(String timeOfEventSent, String sendSmsSent, String sts_deliverySent){
        int _Id = 0 ;

        // ==== Kirim data baru
        TempDeliverySmsSent tempDeliverySmsSent = new TempDeliverySmsSent();
        tempDeliverySmsSent.time_sms = timeOfEventSent;
        tempDeliverySmsSent.sms_interval = sendSmsSent;
        tempDeliverySmsSent.status_delivery = sts_deliverySent;

        tempDeliverySmsSent.id=_Id;
        if(sendSmsSent != null) {
            crudTempDeliverySmsSent.insert(tempDeliverySmsSent);
            rowCont = null;
            timeOfEvent = null;
            crudSmsLoc.deleteAll();
            Log.d(TAG, "Sukses " + sendSmsSent);
        }
        // ==== End

        // ==== Kirim data lama yang belum ke kirim
        TempDeliverySmsSent tempDeliverySmsFailed = new TempDeliverySmsSent();
        tempDeliverySmsFailed.time_sms = timeOfEventFailed;
        tempDeliverySmsFailed.sms_interval = data_sms_failed;
        tempDeliverySmsFailed.status_delivery = "Sukses";

        if(data_sms_failed != null) {
            crudTempDeliverySmsSent.insert(tempDeliverySmsFailed);
            timeOfEventFailed = null;
            data_sms_failed = null;
            crudTempDeliverySmsFailed.deleteFirstRow();
            Log.d(TAG, "Insert Data Sms Yang gagal " + data_sms_failed);
        }
        // ==== End

    }

    // ===========================================================================
    Runnable timerRunnableFailed = new Runnable() {
        @Override
        public void run() {
            validatData();
            timerHandler.postDelayed(this, 30000);
        }
    };

    private void validatData() {
        rowContFailed = crudTempDeliverySmsFailed.getRowCount();
        if (rowContFailed != 0) {
            ArrayList<HashMap<String, String>> smsFailed = crudTempDeliverySmsFailed.getData();
            for (Map<String, String> sms : smsFailed) {
                idFailed = Integer.parseInt(sms.get(KEY_ID));
                timeOfEventFailed = sms.get(KEY_TIME_SMS);
                data_sms_failed = sms.get(KEY_SMS_INTERVAL);

                smsMgr.sendTextMessage(numberDestination, null, data_sms_failed, sentPI, null);
                Log.d(TAG, " data_sms_failed " + data_sms_failed);
            }

        }
    }

}
