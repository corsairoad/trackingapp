package intan.steelytoe.com.ui.activity.list;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import intan.steelytoe.com.model.TempDeliverySmsSent;
import intan.steelytoe.com.sqllite.DBHelper;

import java.util.ArrayList;

import static intan.steelytoe.com.model.TempDeliverySmsSent.KEY_SMS_INTERVAL;
import static intan.steelytoe.com.model.TempDeliverySmsSent.KEY_TIME_SMS;
import static intan.steelytoe.com.model.TempDeliverySmsSent.KEY_STATUS_DELIVERY;

public class ListViewSmsSent extends ListActivity {
	
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
					TempDeliverySmsSent.TABLE, null);

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