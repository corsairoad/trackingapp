package intan.steelytoe.com.ui.activity.list;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import intan.steelytoe.com.model.TempDeliverySmsFailed;
import intan.steelytoe.com.sqllite.DBHelper;

import static intan.steelytoe.com.model.TempDeliverySmsFailed.KEY_SMS_INTERVAL;
import static intan.steelytoe.com.model.TempDeliverySmsFailed.KEY_STATUS_DELIVERY;
import static intan.steelytoe.com.model.TempDeliverySmsFailed.KEY_TIME_SMS;

/**
 * Created by haiv on 03/04/17.
 */

public class ListViewSmsFailed extends ListActivity {
    private ArrayList<String> results = new ArrayList<String>();
    private SQLiteDatabase newDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openAndQueryDatabase();

        displayResultList();


    }
    private void displayResultList() {
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, results));
        getListView().setTextFilterEnabled(true);

    }
    private void openAndQueryDatabase() {
        try {
            DBHelper dbHelper = new DBHelper(this.getApplicationContext());
            newDB = dbHelper.getWritableDatabase();
            Cursor c = newDB.rawQuery("SELECT * FROM " +
                    TempDeliverySmsFailed.TABLE, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        String time = c.getString(c.getColumnIndex(KEY_TIME_SMS));
                        String sms = c.getString(c.getColumnIndex(KEY_SMS_INTERVAL));
                        String sts = c.getString(c.getColumnIndex(KEY_STATUS_DELIVERY));
                        results.add("sms: " + time + " => " + sms  + " => Status " + sts);
                    } while (c.moveToNext());
                }
            }
        } catch (SQLiteException se ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }

    }
}
