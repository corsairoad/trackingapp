package tamborachallenge.steelytoe.com.common.events;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by haiv on 04/04/17.
 */

public class alarmSmsSent extends BroadcastReceiver {
    private static final String TAG = ServiceSendSms.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int resultCode = getResultCode();
        Log.d(TAG, "resultCode " + resultCode);

        switch (resultCode) {
            case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS sent", Toast.LENGTH_LONG).show();
//                sts_delivery = "Sent";
//                insertToSQLiteDatabase_SmsSent(timeOfEvent, rowCont, sts_delivery);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "Generic failure", Toast.LENGTH_LONG).show();
//                sts_delivery = "Generic failure";
//                insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(context, "No service", Toast.LENGTH_LONG).show();
//                sts_delivery = "No service";
//                insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "Null PDU", Toast.LENGTH_LONG).show();
//                sts_delivery = "Null PDU";
//                insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(context, "Radio off", Toast.LENGTH_LONG).show();
//                sts_delivery = "Radio off";
//                insertToSQLiteDatabase_SmsFailed(timeOfEvent, rowCont, sts_delivery);
                break;
        }
    }
}
