package tamborachallenge.steelytoe.com.ui.activity.list;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import tamborachallenge.steelytoe.com.model.TempLoaction;
import tamborachallenge.steelytoe.com.sqllite.DBHelper;

import java.util.ArrayList;

import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lat;
import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_lng;
import static tamborachallenge.steelytoe.com.model.TempLoaction.KEY_timer;
/**
 * Created by haiv on 23/03/17.
 */

public class ListViewLoc extends ListActivity {
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
                    TempLoaction.TABLE, null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    do {
                        String time = c.getString(c.getColumnIndex(KEY_timer));
                        String lat = c.getString(c.getColumnIndex(KEY_lat));
                        String lng = c.getString(c.getColumnIndex(KEY_lng));
                        results.add(time + " ==> Lat " + lat  + "  Lng" + lng);
                    } while (c.moveToNext());
                }
            }
        } catch (SQLiteException se ) {
            Log.e(getClass().getSimpleName(), "Could not create or Open the database");
        }

    }

}
