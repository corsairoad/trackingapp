package intan.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import intan.steelytoe.com.model.RunningDetail;
import intan.steelytoe.com.sqllite.DBHelper;

/**
 * Created by fadlymunandar on 7/11/17.
 */

public class CrudRunningDetail {

    private Context context;
    private static CrudRunningDetail crudRunningDetail;
    private SQLiteDatabase db;
    private DecimalFormat decimalFormat;

    private CrudRunningDetail(Context context) {
        this.context = context.getApplicationContext();
        db = new DBHelper(this.context).getWritableDatabase();
        decimalFormat = new DecimalFormat("####.##");
    }

    public static CrudRunningDetail getInstance(Context context) {
        if (crudRunningDetail == null) {
            crudRunningDetail = new CrudRunningDetail(context);
        }
        return crudRunningDetail;
    }

    public void setRunningDetail(String runningId, String dateTime, String distance, String duration, String dateHeader) {
        ContentValues cv = new ContentValues();

        cv.put(RunningDetail.Table.COL_RUNNING_ID, runningId);
        cv.put(RunningDetail.Table.COL_DATE_TIME, dateTime);
        cv.put(RunningDetail.Table.COL_DISTANCE, distance);
        cv.put(RunningDetail.Table.COL_DURATION, duration);
        cv.put(RunningDetail.Table.COL_DATE_HEADER, dateHeader);

        db.insert(RunningDetail.Table.TABLE_NAME, null, cv);
    }

    public List<RunningDetail> getRunningDetailsByDateHeader(String dateHeader) {
        List<RunningDetail> runningDetails = new ArrayList<>();
        String selection = RunningDetail.Table.COL_DATE_HEADER + " = ?";
        String[] cols = new String[]{};
        String[] args = new String[] {dateHeader};

        Cursor cursor = db.query(RunningDetail.Table.TABLE_NAME, cols,selection, args,null,null, RunningDetail.Table.COL_ID);

        if (cursor.moveToFirst()) {
            do {
                RunningDetail runningDetail = new RunningDetail();

                String distance = cursor.getString(cursor.getColumnIndex(RunningDetail.Table.COL_DISTANCE));
                Double floatDistance = Double.valueOf(distance);
                distance = decimalFormat.format(floatDistance);
                String readableDuration = getReadableDuration(cursor.getString(cursor.getColumnIndex(RunningDetail.Table.COL_DURATION)));

                runningDetail.setRunningId(cursor.getString(cursor.getColumnIndex(RunningDetail.Table.COL_RUNNING_ID)));
                runningDetail.setDateTime(cursor.getString(cursor.getColumnIndex(RunningDetail.Table.COL_DATE_TIME)));
                runningDetail.setDistance(distance);
                runningDetail.setDuration(readableDuration);
                runningDetail.setDateHeader(dateHeader);

                runningDetails.add(runningDetail);

            }while (cursor.moveToNext());
        }

        return runningDetails;
    }

    private String getReadableDuration(String duration) {
        if (duration != null) {
            StringBuilder sb = new StringBuilder();
            String[] durations = duration.trim().split(":");
            int index = 0;

            for (String s : durations) {
                int val = Integer.valueOf(s);
                switch (index) {
                    case 0:
                        String hour = val>0? "" + val + "h" : "";
                        sb.append(hour).append(" ");
                        break;
                    case 1:
                        String min = val > 0? "" + val + "m" : "";
                        sb.append(min).append(" ");
                        break;
                    case 2:
                        String sec = val>0? "" + val + "s":"";
                        sb.append(sec);
                        break;
                }
                index++;
            }
            return sb.toString();
        }

        return "";
    }
}
