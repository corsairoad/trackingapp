package tamborachallenge.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed;
import tamborachallenge.steelytoe.com.model.TempLoaction;
import tamborachallenge.steelytoe.com.sqllite.DBHelper;

import static tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed.KEY_ID;
import static tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed.KEY_TIME_SMS;
import static tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed.KEY_SMS_INTERVAL;
import static tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed.KEY_STATUS_DELIVERY;

/**
 * Created by haiv on 22/03/17.
 */

public class CrudTempDeliverySmsFailed {
    private DBHelper dbHelper;

    public CrudTempDeliverySmsFailed(Context context){
        dbHelper = new DBHelper(context);
    }

    //check count row
    public int getRowCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM " + TempDeliverySmsFailed.TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }

    public ArrayList<HashMap<String, String>> getData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  * FROM " + TempDeliverySmsFailed.TABLE;

        ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
//            do {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("id", cursor.getString(cursor.getColumnIndex(KEY_ID)));
                data.put("time_sms", cursor.getString(cursor.getColumnIndex(KEY_TIME_SMS)));
                data.put("sms_interval", cursor.getString(cursor.getColumnIndex(KEY_SMS_INTERVAL)));
                data.put("status_delivery", cursor.getString(cursor.getColumnIndex(KEY_STATUS_DELIVERY)));
                listData.add(data);
//            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listData;
    }

    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TempDeliverySmsFailed.TABLE,null,null);
        db.close(); // Closing database connection
    }

    public int insert(TempDeliverySmsFailed tempDeliverySmsFailed){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TempDeliverySmsFailed.KEY_TIME_SMS, tempDeliverySmsFailed.time_sms);
        contentValues.put(TempDeliverySmsFailed.KEY_SMS_INTERVAL, tempDeliverySmsFailed.sms_interval);
        contentValues.put(TempDeliverySmsFailed.KEY_STATUS_DELIVERY, tempDeliverySmsFailed.status_delivery);

        //insert row
        long id =  sqLiteDatabase.insert(TempDeliverySmsFailed.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return (int) id;
    }

    // Deleting a shop
    public void deleteOneData(TempDeliverySmsFailed tempDeliverySmsFailed) {
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        sqLiteDatabase.delete(TempDeliverySmsFailed.TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(tempDeliverySmsFailed.getId()) });
        sqLiteDatabase.close();
    }

    public void deleteFirstRow()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TempDeliverySmsFailed.TABLE, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            String rowId = cursor.getString(cursor.getColumnIndex(KEY_ID));
            db.delete(TempDeliverySmsFailed.TABLE, KEY_ID + "=?",  new String[]{rowId});
        }
        db.close();
    }
}
