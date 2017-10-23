package intan.steelytoe.com.model;

/**
 * Created by fadlymunandar on 7/11/17.
 */

public class RunningDetail extends RunningHeaderHistory {

    private String runningId;
    private String dateTime;
    private String dateHeader;
    private String distance;
    private String duration;

    public String getRunningId() {
        return runningId;
    }

    public void setRunningId(String runningId) {
        this.runningId = runningId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDateHeader() {
        return dateHeader;
    }

    public void setDateHeader(String dateHeader) {
        this.dateHeader = dateHeader;
    }

    public static class Table {

        public static final String TABLE_NAME = "running_detail";

        public static final String COL_ID = "_id";
        public static final String COL_RUNNING_ID = "running_id";
        public static final String COL_DATE_TIME = "datetime";
        public static final String COL_DISTANCE = "distance";
        public static final String COL_DURATION = "duration";
        public static final String COL_DATE_HEADER = "date_header";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +  COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RUNNING_ID + " TEXT, " +
                COL_DATE_TIME + " TEXT, " +
                COL_DISTANCE + " TEXT, " +
                COL_DURATION + " TEXT, " +
                COL_DATE_HEADER + " TEXT);";
    }
}
