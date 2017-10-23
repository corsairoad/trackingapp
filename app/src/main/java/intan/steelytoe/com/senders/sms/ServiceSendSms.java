package intan.steelytoe.com.senders.sms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by dki on 02/03/17.
 */

public class ServiceSendSms {
    private static ServiceSendSms rrS;
    public static final String SENT = "SMS_SENT";
    public static final String DELIVERED = "SMS_DELIVERED";

    private ServiceSendSms(final Context ctx){
        //when the SMS has been sent
        ctx.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(ctx, "SMS Enviado",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(ctx, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(ctx, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(ctx, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(ctx, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage("085730748514", null, "Service SMS ", null, null);

        //when the SMS has been delivered
        ctx.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(ctx, "SMS Entregado",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(ctx, "SMS No Entregado",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
    }

    public static ServiceSendSms getInstance(final Context ctx){
        if(rrS==null){
            rrS = new ServiceSendSms(ctx);
        }
        return rrS;
    }
}
