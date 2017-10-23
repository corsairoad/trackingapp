package intan.steelytoe.com.senders.opengts;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import intan.steelytoe.com.common.Maths;
import intan.steelytoe.com.common.SerializableLocation;
import intan.steelytoe.com.common.events.ServiceSendSms;
import intan.steelytoe.com.model.TempSmsLoc;
import intan.steelytoe.com.model.User;
import intan.steelytoe.com.sqllite.DBHelper;
import intan.steelytoe.com.util.PrefManager;
import intan.steelytoe.com.util.UserManager;

import static intan.steelytoe.com.model.TempSmsLoc.KEY_SMS;

/**
 * Created by dki on 27/02/17.
 */

public class OpenGTSManager{
    private static final String TAG = OpenGTSManager.class.getSimpleName();
    TempSmsLoc tempSmsLoc = new TempSmsLoc();
    Context context;

    private static final String SEND_METHOD_SMS = "sms";
    private static final String SEND_METHOD_DATA = "data";
    private static final String SEND_METHOD_DATA_SMS = "datasms";
    private DBHelper dbHelper;
    private User user;

    public OpenGTSManager (Context context){
        dbHelper = new DBHelper(context);
        this.user = UserManager.getInstance(context).getUser();
    }

    public void sendLocations(SerializableLocation[] locations, Context context){
        if (locations.length > 0) {

            /*String server__ = PreferenceHelper.getOpenGTSServer();
            MaterialEditTextPreference txtOpenGTSServer = (MaterialEditTextPreference) findPreference("opengts_server");
            Log.d("server_", "" + server__);
            int port = Integer.parseInt(preferenceHelper.getOpenGTSServerPort());
            String path = preferenceHelper.getOpenGTSServerPath();
            String deviceId = preferenceHelper.getOpenGTSDeviceId();
            String accountName = preferenceHelper.getOpenGTSAccountName();
            String communication = preferenceHelper.getOpenGTSServerCommunicationMethod();*/

//            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//            String serverString =  sharedPreferences.getString("opengts_server", null);
////            String portString =  sharedPreferences.getString("opengts_server_port", null);
////            int port = Integer.parseInt(portString);
////            String communication =  sharedPreferences.getString("opengts_server_communication_method", null);
////            String path =  sharedPreferences.getString("autoopengts_server_path", null);
////            String deviceId =  sharedPreferences.getString("opengts_device_id", null);
////            String accountName =  sharedPreferences.getString("opengts_accountname", null);

            this.context = context;
            String server = "www.steelytoe.com";
            int port = 443;
            String path = "api/data/marker/receive";
            String deviceId = user.getCode();
            String accountName = user.getEmail();
            String communication = "https";

            if(communication.equalsIgnoreCase("udp")){
                Log.d(TAG, "comunication not https");
            } else {
                sendByHttp(deviceId, accountName, locations, communication, path, server, port);
            }
        }
    }

    void sendByHttp(String deviceId, String accountName, SerializableLocation[] locations, String communication, String path, String server, int port) {
        for(SerializableLocation loc:locations){
            String finalUrl = getUrl(deviceId, accountName, loc, communication, path, server, port );
            saveSmsData(loc);
            filterSendMethod(finalUrl);
            //new OpenGTSManager.DownloadTask().execute(finalUrl);
            Log.d(TAG, "final URL " + finalUrl);
        }
    }

    private void filterSendMethod(final String finalUrl) {
        String sendMethod = PrefManager.getInstance(this.context).getSendLocationMethod();
        switch (sendMethod) {
            case SEND_METHOD_DATA:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            downloadContent(finalUrl);
                            Log.d(TAG, "LOCATION DATA SENT VIA INTERNET DATA");
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;

            case SEND_METHOD_DATA_SMS:
                new OpenGTSManager.DownloadTask().execute(finalUrl);
                Log.d(TAG,"LOCATION DATA SENT VIA SMS AND INTERNET DATA");
                break;
            case SEND_METHOD_SMS:
                startSendSmsService();
                Log.d(TAG,"LOCATION DATA SENT VIA SMS");
                break;
            default:
                new OpenGTSManager.DownloadTask().execute(finalUrl);
                Log.d(TAG,"LOCATION DATA SENT VIA DEFAULT METHOD (SMS AND INTERNET)");
                break;

        }
    }

    private void saveSmsData(SerializableLocation loc) {
                    /*double time = loc.getTime();*/
        double perseribu = loc.getTime() / 1000;
        DecimalFormat df = new DecimalFormat("#");
            /*int seconds = (int) (time / 1000) % 60 ;
            int minutes = (int) ((time / (1000*60)) % 60);
            int hours   = (int) ((time / (1000*60*60)) % 24);*/

            /*=============PROSES SEND SMS*/
        DecimalFormat latlng = new DecimalFormat("#.######");
        DecimalFormat accu = new DecimalFormat("#.##");
        String contentSms = String.valueOf(df.format(perseribu))+"$"
                + String.valueOf(latlng.format(loc.getLatitude()))+"$"
                + String.valueOf(latlng.format(loc.getLongitude()))+"$"
                + String.valueOf(accu.format(loc.getAccuracy()))+"$"
                + String.valueOf(accu.format(loc.getAltitude()))+"$"
                + String.valueOf(accu.format(loc.getSpeed()))+";";


        tempSmsLoc.sms = contentSms;
        int stsInsert = insert(tempSmsLoc);
        Log.d(TAG, "Insert Data SMS " + stsInsert);
    }

    private void startSendSmsService() {
        Intent intent = new Intent(context, ServiceSendSms.class);
        context.startService(intent);
    }

    public static String gprmcEncode(SerializableLocation loc) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        DecimalFormat f = new DecimalFormat("0.000000", dfs);

        String gprmc = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,,",
                "$GPRMC",
                getNmeaGprmcTime(new Date(loc.getTime())),
                "A",
                getNmeaGprmcCoordinates(Math.abs(loc.getLatitude())),
                (loc.getLatitude() >= 0) ? "N" : "S",
                getNmeaGprmcCoordinates(Math.abs(loc.getLongitude())),
                (loc.getLongitude() >= 0) ? "E" : "W",
                f.format(Maths.mpsToKnots(loc.getSpeed())),
                f.format(loc.getBearing()),
                getNmeaGprmcDate(new Date(loc.getTime()))
        );

        gprmc += "*" + getNmeaChecksum(gprmc);

        return gprmc;
    }


    public static String getNmeaGprmcTime(Date dateToFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(dateToFormat);
    }

    public static String getNmeaGprmcDate(Date dateToFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(dateToFormat);
    }

    public static String getNmeaGprmcCoordinates(double coord) {
        // “DDDMM.MMMMM”
        int degrees = (int) coord;
        double minutes = (coord - degrees) * 60;

        DecimalFormat df = new DecimalFormat("00.00000", new DecimalFormatSymbols(Locale.US));
        StringBuilder rCoord = new StringBuilder();
        rCoord.append(degrees);
        rCoord.append(df.format(minutes));

        return rCoord.toString();
    }


    public static String getNmeaChecksum(String msg) {
        int chk = 0;
        for (int i = 1; i < msg.length(); i++) {
            chk ^= msg.charAt(i);
        }
        String chk_s = Integer.toHexString(chk).toUpperCase();
        while (chk_s.length() < 2) {
            chk_s = "0" + chk_s;
        }
        return chk_s;
    }


    public static String getUrl(String id, String accountName, SerializableLocation loc, String communication, String path, String server, int port) {
        List<AbstractMap.SimpleEntry<String,String>> qparams = new ArrayList<>();
        qparams.add(new AbstractMap.SimpleEntry<>("id", id));
        qparams.add(new AbstractMap.SimpleEntry<>("dev", id));
        qparams.add(new AbstractMap.SimpleEntry<>("acct", accountName));

        /*if (!Strings.isNullOrEmpty(accountName)) {
            qparams.add(new AbstractMap.SimpleEntry<>("acct", accountName));
        } else {
            qparams.add(new AbstractMap.SimpleEntry<>("acct", id));
        }*/

        qparams.add(new AbstractMap.SimpleEntry<>("batt", "0"));
        qparams.add(new AbstractMap.SimpleEntry<>("code", "0xF020"));
        qparams.add(new AbstractMap.SimpleEntry<>("alt", String.valueOf(loc.getAltitude())));
        qparams.add(new AbstractMap.SimpleEntry<>("gprmc", OpenGTSManager.gprmcEncode(loc)));

        if(path.startsWith("/")){
            path = path.replaceFirst("/","");
        }

        return String.format("%s://%s:%d/%s?%s",communication.toLowerCase(),server,port,path,getQuery(qparams));

    }

    private static String getQuery(List<AbstractMap.SimpleEntry<String, String>> params){
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry<String, String> pair : params){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(pair.getKey());
            result.append("=");
            result.append(pair.getValue());
        }

        return result.toString();
    }


    //=========================================================================================
    //=============================== PROSES DB ===============================================
    //=========================================================================================


    public int insert(TempSmsLoc tempSmsLoc){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SMS, tempSmsLoc.sms);

        //insert row
        long id =  sqLiteDatabase.insert(tempSmsLoc.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return (int) id;
    }


    //=========================================================================================
    //=============================== Check Device ID =========================================
    //=========================================================================================
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "4444";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            int tes = Integer.parseInt(result);
            if(tes == 200){
                Log.d(TAG, "Sukses " + result);
            } else {
                startSendSmsService();
                Log.d(TAG, "Gagal " + result);
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
