package intan.steelytoe.com.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.LocationRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

import intan.steelytoe.com.model.TempLoaction;

/**
 * Created by fadlymunandar on 5/20/17.
 */

public class PrefManager {

    private Context context;
    private static PrefManager prefManager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_SECOND= "second";
    public static final String KEY_STILL = "still";
    public static final String KEY_STARTED_TIME = "started_time";
    public static final String KEY_LAST_DISTANCE = "last_distance";
    public static final String KEY_PREF_ACCURACY = "pref_accuracy";
    public static final String KEY_PREF_SEND_LOC_DATA = "pref_send_location_data";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_CODE = "user_code";
    public static final String KEY_DATE_HEADER = "date_header";


    private PrefManager(Context context) {
        this.context = context.getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        editor = preferences.edit();
    }

    public static PrefManager getInstance(Context context) {
        if (prefManager == null){
            prefManager = new PrefManager(context);
        }
        return prefManager;
    }

    public void saveHour(int hour){
        editor.putInt(KEY_HOUR, hour);
        editor.apply();
    }

    public void saveMinute(int minute) {
        editor.putInt(KEY_MINUTE, minute);
        editor.apply();
    }

    public void saveSecond(int second) {
        editor.putInt(KEY_SECOND, second);
        editor.apply();
    }

    public int getHour(){
        return preferences.getInt(KEY_HOUR, 0);
    }

    public int getMinute(){
        return preferences.getInt(KEY_MINUTE, 0);
    }

    public int getSecond() {
        return preferences.getInt(KEY_SECOND, 0);
    }

    public void resetTime(){
        saveHour(0);
        saveMinute(0);
        saveSecond(0);
    }

    public int getStillFlagActivity() {
        return preferences.getInt(KEY_STILL, 0);
    }

    public void addStillFlagActivity(int count) {
        editor.putInt(KEY_STILL, count);
        editor.commit();
    }

    public void setStartedTime(String startedTime) {
        editor.putString(KEY_STARTED_TIME, startedTime);
        editor.commit();
    }

    public String getStartedTime() {
        return preferences.getString(KEY_STARTED_TIME, null);
    }

    public float getLastDistance() {
        return preferences.getFloat(KEY_LAST_DISTANCE, 0);
    }

    public void setLastDistance(float lastDistance) {
        editor.putFloat(KEY_LAST_DISTANCE, lastDistance);
        editor.commit();
    }

    public int getLocationAccuracyPriority(){
        int accuracyPriority;
        String accuracyValue = preferences.getString(KEY_PREF_ACCURACY, null);

        switch (accuracyValue) {
            case "HIGH":
                accuracyPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                break;
            case "BALANCED":
                accuracyPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                break;
            case "LOW":
                accuracyPriority = LocationRequest.PRIORITY_LOW_POWER;
                break;
            default:
                accuracyPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        }

        return accuracyPriority;
    }

    public String getSendLocationMethod() {
        return preferences.getString(KEY_PREF_SEND_LOC_DATA, null);
    }

    public void setUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.commit();
    }

    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, null);
    }

    public void setUserCode(String code) {
        editor.putString(KEY_USER_CODE, code);
        editor.commit();
    }

    public String getUserCode() {
        return preferences.getString(KEY_USER_CODE, null);
    }

    public void setRunningId(String runningId) {
        if (runningId == null) {
            runningId = getCurrentDateTime();
        }
        editor.putString(TempLoaction.KEY_RUNNING_ID, runningId);
        editor.commit();
    }

    public String getRunningId() {
        return preferences.getString(TempLoaction.KEY_RUNNING_ID, null);
    }

    public void removeRunningId() {
        editor.putString(TempLoaction.KEY_RUNNING_ID, null);
    }

    // for ID tempLocation
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("DDMMyyhhmmss");
        return sdf.format(new Date());
    }

    // for header date
    private String getHeader() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        return sdf.format(new Date());
    }

    public void setDateHeader() {
        editor.putString(KEY_DATE_HEADER, getHeader());
        editor.commit();
    }

    public String getDateHeader() {
        return preferences.getString(KEY_DATE_HEADER, getHeader());
    }

}
