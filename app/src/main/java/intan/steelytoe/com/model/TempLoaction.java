package intan.steelytoe.com.model;


/**
 * Created by hadi on 03/02/2017.
 */

public class TempLoaction {

    // Labels table name
    public static final String TABLE = "temp_location";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_lat = "lat";
    public static final String KEY_lng = "lng";
    public static final String KEY_timer = "timer";
    public static final String KEY_RUNNING_ID = "running_id";
    public static final String KEY_DATE = "date";

    // property help us to keep data
    public int id;
    public String lat;
    public String lng;
    public String timer;
    public String runningId;
    public String date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getRunningId() {
        return runningId;
    }

    public void setRunningId(String runningId) {
        this.runningId = runningId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
