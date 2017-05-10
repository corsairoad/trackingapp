package tamborachallenge.steelytoe.com.ui.activity.permission;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import tamborachallenge.steelytoe.com.MainActivity;
import tamborachallenge.steelytoe.com.R;
import tamborachallenge.steelytoe.com.common.CheckNetwork;
import tamborachallenge.steelytoe.com.common.NetworkUtil;

public class InisialisasiActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = InisialisasiActivity.class.getSimpleName();
    LocationManager locationManager;
    TextView textviewDevice, textviewLat, textviewLng, textviewIsiSms, textviewStsSms, textviewCheckDeviceID;
    Button btnSendSms;
    String numberDestination, isiSms = "", sts_delivery, SENT = "SMS_SENT", provider, android_id, deviceidString;
    int deviceidInt;
    PendingIntent sentPI;
    SmsManager smsMgr = SmsManager.getDefault();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inisialisasi);

        textviewDevice = (TextView) findViewById(R.id.fieldDeviceId);
        textviewLat = (TextView)findViewById(R.id.fieldLatitude);
        textviewLng = (TextView)findViewById(R.id.fieldLongitude);
        textviewIsiSms = (TextView)findViewById(R.id.fieldIsiSms);
        textviewStsSms = (TextView)findViewById(R.id.fieldStatusSms);
        textviewCheckDeviceID = (TextView)findViewById(R.id.fieldCheckDeviceID);

        btnSendSms = (Button) findViewById(R.id.btnSendSms);

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        textviewDevice.setText(android_id);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (provider != null && !provider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 1000 * 5, 1, this);
            if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        } else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

        sentPI = PendingIntent.getBroadcast(this, 2, new Intent(SENT), 0);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(SENT));

        /*Check Phone Nomber*/
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        numberDestination = sharedPreferences.getString("sms_server_number", null); // getting String
        if (numberDestination == null || numberDestination == "") {
            editor.putString("sms_server_number", "082211122203");
            editor.commit();
            numberDestination = "082211122203";
        }

        deviceidString =  sharedPreferences.getString("device_id", null); // getting String
        if(deviceidString == null || deviceidString == "" || deviceidString == "0"){
            editor.putString("device_id", "0");
            editor.commit();
            deviceidInt = 0;
            Toast.makeText(getApplicationContext(),"nol " + deviceidString, Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(InisialisasiActivity.this, MainActivity.class));
            finish();
        }

        /*Btn Proses Send Sms*/
        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = NetworkUtil.getConnectivityStatusString(getApplicationContext());
                if(status == "Wifi enabled"){
                    sendSms();
                }else if (status == "Mobile data enabled"){
                    sendSms();
                } else {
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBatInfoReceiver);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        String stringLatitude = String.valueOf(location.getLatitude());
        textviewLat.setText(stringLatitude);

        String stringLongitude = String.valueOf(location.getLongitude());
        textviewLng.setText(stringLongitude);

        // Isi sms
        isiSms = "";
        String deviceId = textviewDevice.getText().toString();
        String lat = textviewLat.getText().toString();
        String lng = textviewLng.getText().toString();
        isiSms += "DEVICEID="+deviceId+"&LOCATION="+lat+","+lng;
        textviewIsiSms.setText(isiSms);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
    }


    // ========================================================================================= SEND SMS

    private void sendSms() {
        if (isSimExists()) {
            try {
                smsMgr.sendTextMessage(numberDestination, null, isiSms, sentPI, null);
            } catch (Exception e) {
                sts_delivery = "Failed to send SMS";
                textviewStsSms.setText(sts_delivery);
                e.printStackTrace();
            }
        } else {
            sts_delivery = "Cannot send SMS";
            textviewStsSms.setText(sts_delivery);
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
                    textviewStsSms.setText(sts_delivery);
                    new DownloadTask().execute("https://www.steelytoe.com/api/public/user/getByAndroidId/" + android_id);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    sts_delivery = "Generic failure";
                    textviewStsSms.setText(sts_delivery);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    sts_delivery = "No service";
                    textviewStsSms.setText(sts_delivery);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    sts_delivery = "Null PDU";
                    textviewStsSms.setText(sts_delivery);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    sts_delivery = "Radio off";
                    textviewStsSms.setText(sts_delivery);
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
                case TelephonyManager.SIM_STATE_ABSENT:
                    sts_delivery = "No Sim Found!";
                    textviewStsSms.setText(sts_delivery);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sts_delivery = "Network Locked!";
                    textviewStsSms.setText(sts_delivery);
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sts_delivery = "PIN Required to access SIM!";
                    textviewStsSms.setText(sts_delivery);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sts_delivery = "PUK Required to access SIM!";
                    textviewStsSms.setText(sts_delivery);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sts_delivery = "Unknown SIM State!";
                    textviewStsSms.setText(sts_delivery);
                    break;
            }
            return false;
        }
    }

    // =============================================== Check Device ID
    private class DownloadTask extends AsyncTask<String, Void, String> {
//        private ProgressDialog pdia;
//        Context context;
//
//        protected void onPreExecute(){
//            super.onPreExecute();
//            pdia = new ProgressDialog(context);
//            pdia.setMessage("Loading...");
//            pdia.show();
//        }

        @Override
        protected String doInBackground(String... params) {
            /*return "Unable to retrieve data. URL may be invalid.";*/

            if (new CheckNetwork(InisialisasiActivity.this).isNetworkAvailable()) {
                try {
                    return downloadContent(params[0]);
                } catch (IOException e) {
                    return "4444";
                }

            } else {
                // No Internet
                 Toast.makeText(InisialisasiActivity.this, "no internet!", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            int tes = Integer.parseInt(result);
            if(tes == 200){
                editor.putString("device_id", result);
                editor.commit();
                startActivity(new Intent(InisialisasiActivity.this, MainActivity.class));
                finish();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                textviewCheckDeviceID.setText(result);
            } else {
                Toast.makeText(getApplicationContext(), "Check di Steeltoe.com", Toast.LENGTH_LONG).show();
                textviewCheckDeviceID.setText("Unable to retrieve data. URL may be invalid.");
            }
        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            String responseString = Integer.toString(response);
            Log.d(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputStreamToString(is, length);
            /*return contentAsString;*/
            return responseString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }

}
