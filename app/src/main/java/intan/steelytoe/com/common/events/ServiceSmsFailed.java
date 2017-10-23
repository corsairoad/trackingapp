package intan.steelytoe.com.common.events;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import intan.steelytoe.com.common.Impl.CrudTempDeliverySmsFailed;
import intan.steelytoe.com.common.Impl.CrudTempDeliverySmsSent;

import static intan.steelytoe.com.model.TempDeliverySmsFailed.KEY_ID;
import static intan.steelytoe.com.model.TempDeliverySmsFailed.KEY_TIME_SMS;
import static intan.steelytoe.com.model.TempDeliverySmsFailed.KEY_SMS_INTERVAL;

public class ServiceSmsFailed extends Service {
    private static final String TAG = ServiceSmsFailed.class.getSimpleName();
    Handler timerHandlerFailed = new Handler();
    CrudTempDeliverySmsFailed crudTempDeliverySmsFailed = new CrudTempDeliverySmsFailed(this);
    CrudTempDeliverySmsSent crudTempDeliverySmsSent = new CrudTempDeliverySmsSent(this);
    int rowContFailed, idFailed;
    String numberDestination, timeOfEvent, sts_delivery, SimState = "", SENT = "SMS_SENT", data_sms_failed = null, timeSmsFailed;
    PendingIntent sentPI__;
    SmsManager smsMgr = SmsManager.getDefault();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        timerHandlerFailed.postDelayed(timerRunnableFailed, 0);

        /*Check Phone Nomber*/
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        numberDestination = sharedPreferences.getString("sms_server_number", null); // getting String
//        if (numberDestination == null || numberDestination == "") {
//            editor.putString("sms_server_number", "082211122203");
//            editor.commit();
//            numberDestination = "082211122203";
//        }

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
//        sentPI__ = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
//        this.registerReceiver(this.mBatInfoReceiverSmsFailed, new IntentFilter(SENT));
    }

    @Override
    public void onDestroy() {
//        timerHandlerFailed.removeCallbacks(timerRunnableFailed);
//        unregisterReceiver(mBatInfoReceiverSmsFailed);
        super.onDestroy();
    }

    ///=========================================================================================================
    Runnable timerRunnableFailed = new Runnable() {
        @Override
        public void run() {
            /*Time*/
            Time now = new Time();
            now.setToNow();
            timeOfEvent = now.format("%H:%M:%S");
            Log.d(TAG, "timerRunnable ");
            validatData();
            timerHandlerFailed.postDelayed(this, 30000);
        }
    };

    private void validatData(){
        rowContFailed = crudTempDeliverySmsFailed.getRowCount();
        Log.d(TAG, "rowCont" + rowContFailed);
        if (rowContFailed != 0) {
            ArrayList<HashMap<String, String>> smsFailed = crudTempDeliverySmsFailed.getData();
            for (Map<String, String> sms : smsFailed) {
                idFailed = Integer.parseInt(sms.get(KEY_ID));
                timeSmsFailed = sms.get(KEY_TIME_SMS);
                data_sms_failed = sms.get(KEY_SMS_INTERVAL);

                smsMgr.sendTextMessage(numberDestination, null, data_sms_failed, sentPI__, null);
            }
            Toast.makeText(getBaseContext(), "Sms sent!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "No sms failed!", Toast.LENGTH_SHORT).show();
        }
    }


    private final BroadcastReceiver mBatInfoReceiverSmsFailed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            int resultCode = getResultCode();
            if(intent.getAction() != null) {
                Log.d(TAG, "resultCode " + resultCode);
            }

//            switch (resultCode) {
//                case Activity.RESULT_OK:
//                    sts_delivery = "Sent";
//                    /*insertToSQLiteDatabase_SmsSent__(id, timeSms, data_sms_failed, sts_delivery);*/
//                    break;
//                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                    sts_delivery = "Generic failure";
//                    /*insertToSQLiteDatabase_SmsFailed(timeSms, data_sms_failed, sts_delivery);*/
//                    break;
//                case SmsManager.RESULT_ERROR_NO_SERVICE:
//                    sts_delivery = "No service";
//                    /*insertToSQLiteDatabase_SmsFailed(timeSms, data_sms_failed, sts_delivery);*/
//                    break;
//                case SmsManager.RESULT_ERROR_NULL_PDU:
//                    sts_delivery = "Null PDU";
//                    /*insertToSQLiteDatabase_SmsFailed(timeSms, data_sms_failed, sts_delivery);*/
//                    break;
//                case SmsManager.RESULT_ERROR_RADIO_OFF:
//                    sts_delivery = "Radio off";
//                    /*insertToSQLiteDatabase_SmsFailed(timeSms, data_sms_failed, sts_delivery);*/
//                    break;
//            }
        }
    };

//
//    public boolean isSimExists(){
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        int SIM_STATE = telephonyManager.getSimState();
//
//        if(SIM_STATE == TelephonyManager.SIM_STATE_READY)
//            return true;
//        else {
//            switch(SIM_STATE){
//                case TelephonyManager.SIM_STATE_ABSENT: //SimState = "No Sim Found!";
//                    Toast.makeText(getBaseContext(), "No Sim Found!", Toast.LENGTH_LONG).show();
//                    break;
//                case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = "Network Locked!";
//                    Toast.makeText(getBaseContext(), "SNetwork Locked!", Toast.LENGTH_LONG).show();
//                    break;
//                case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = "PIN Required to access SIM!";
//                    Toast.makeText(getBaseContext(), "PIN Required to access SIM!", Toast.LENGTH_LONG).show();
//                    break;
//                case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = "PUK Required to access SIM!"; // Personal Unblocking Code
//                    Toast.makeText(getBaseContext(), "PUK Required to access SIM!", Toast.LENGTH_LONG).show();
//                    break;
//                case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = "Unknown SIM State!";
//                    Toast.makeText(getBaseContext(), "Unknown SIM State!", Toast.LENGTH_LONG).show();
//                    break;
//            }
//            return false;
//        }
//    }
//
//    // ================================================================================== Insert To Database
//    private final void insertToSQLiteDatabase_SmsSent__(int id, String timeOfEvent, String sendSms, String sts_delivery){
//
//        TempDeliverySmsSent tempDeliverySmsSent = new TempDeliverySmsSent();
//        tempDeliverySmsSent.time_sms = timeOfEvent;
//        tempDeliverySmsSent.sms_interval = sendSms;
//        tempDeliverySmsSent.status_delivery = sts_delivery;
//
//        if(sendSms != null) {
//            crudTempDeliverySmsSent.insert(tempDeliverySmsSent);
//            deleteData(id);
//            Log.d(TAG, "Sukses__SMSFailed " + sendSms);
//        } else {
//            Log.d(TAG, "Sukses__SMSFailed " + sendSms);
//        }
//
//        data_sms_failed = null;
//    }
//
//    private void deleteData(int idDelete){
//        TempDeliverySmsFailed tempDeliverySmsFailed = new TempDeliverySmsFailed();
//        tempDeliverySmsFailed.id = idDelete;
//
//        crudTempDeliverySmsFailed.deleteOneData(tempDeliverySmsFailed);
//    }
}
