package tamborachallenge.steelytoe.com.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.Impl.CrudSmsLoc;
import tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed;
import tamborachallenge.steelytoe.com.ui.fragments.display.GpsMapsFragment;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;

public class ViewTrackActivity extends AppCompatActivity {
    SmsManager smsManager = SmsManager.getDefault();
    FloatingActionButton fab;
    String numberDestination, sts_delivery, timeOfEvent, valueBarcode;
    CrudSmsLoc dataSms = new CrudSmsLoc(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_track);

         /*Check Phone Nomber*/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        numberDestination = sharedPreferences.getString("sms_server_number", null); // getting String
        if (numberDestination == null || numberDestination == "") {
            editor.putString("sms_server_number", "082211122203");
            editor.commit();
            numberDestination = "082211122203";
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Time*/
                Time now = new Time();
                now.setToNow();
                timeOfEvent = now.format("%H:%M:%S");


                // Membuat intent baru untuk memanggil CaptureActivity bawaan ZXing
                Intent captureIntent = new Intent(ViewTrackActivity.this, CaptureActivity.class);
                // Kemudian kita mengeset pesan yang akan ditampilkan ke user saat menjalankan QRCode scanning
                CaptureActivityIntents.setPromptMessage(captureIntent, "Proses Checkin...");
                // Melakukan startActivityForResult, untuk menangkap balikan hasil dari QR Code scanning
                startActivityForResult(captureIntent, 0);


                /*valueBarcode = "129DKjWO736";
                smsManager.sendTextMessage(numberDestination, null, valueBarcode, null, null); // 085730748514  == 082211122203
                sts_delivery = "Sukses";
                insertToSQLiteDatabase(timeOfEvent, valueBarcode, sts_delivery);*/
            }
        });


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_2, GpsMapsFragment.newInstance());
        transaction.commitAllowingStateLoss();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                valueBarcode = data.getStringExtra("SCAN_RESULT");

                smsManager.sendTextMessage(numberDestination, null, valueBarcode, null, null); // 085730748514  == 082211122203
                sts_delivery = "Sukses";
                insertToSQLiteDatabase(timeOfEvent, valueBarcode, sts_delivery);
                Toast.makeText(this,"Checkin Success", Toast.LENGTH_SHORT).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this,"Checkin Failed", Toast.LENGTH_SHORT).show();
            }
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // ================================================================================== Insert To Database
    private final void insertToSQLiteDatabase(String timeOfEvent, String sendSms, String sts_delivery){
        int _Id = 0 ;

        TempDeliverySmsFailed tempDeliverySmsLoc = new TempDeliverySmsFailed();
        tempDeliverySmsLoc.time_sms = timeOfEvent;
        tempDeliverySmsLoc.sms_interval = sendSms;
        tempDeliverySmsLoc.status_delivery = sts_delivery;

        tempDeliverySmsLoc.id=_Id;
        if (_Id == 0 ){
            int id = dataSms.insert(tempDeliverySmsLoc);
            Log.d("Input ", "Input Temp Delivery " + id);
        }

    }
}
