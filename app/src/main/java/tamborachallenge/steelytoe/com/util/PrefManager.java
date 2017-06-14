package tamborachallenge.steelytoe.com.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public static final String PREF_NAME = PrefManager.class.getSimpleName();

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


}
