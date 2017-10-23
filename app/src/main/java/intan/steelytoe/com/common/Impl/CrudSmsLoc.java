package intan.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import intan.steelytoe.com.model.TempDeliverySmsFailed;
import intan.steelytoe.com.model.TempSmsLoc;
import intan.steelytoe.com.sqllite.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static intan.steelytoe.com.model.TempSmsLoc.KEY_SMS;

/**
 * Created by haiv on 25/03/17.
 */

public class CrudSmsLoc {
    private DBHelper dbHelper;

    public CrudSmsLoc(Context context){
        dbHelper = new DBHelper(context);
    }

    //=========================================================================================
    //=============================== PROSES DB TempSmsLoc ====================================
    //=========================================================================================

    //check count row
    public String getRowCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TempSmsLoc.KEY_ID + "," +
                KEY_SMS +
                " FROM " + TempSmsLoc.TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();

        String sendSms = null;

        if(cnt >= 4){
            sendSms = "!1>";
            List<String> penampungKey = new ArrayList<>();
            List<String> penampungValue = new ArrayList<>();

            ArrayList<HashMap<String, String>> listData =  getData();;
            Iterator it = listData.iterator();
            while (it.hasNext()) {
                HashMap<String, String> hashMap = (HashMap<String, String>) it.next();
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    penampungKey.add(key);
                    penampungValue.add(value);
					/*System.out.println(key + "---" + value);*/
                }
            }
			/*System.out.println("--------------------------------------------------------------------------------------------");
			System.out.println("key awal : " + penampungKey.get(0) + " value pertama : " + penampungValue.get(0));
			System.out.println("key tengah : " + penampungKey.get(penampungKey.size()/2) + " value tengah : " + penampungValue.get(penampungKey.size()/2));
			System.out.println("key akhir : " + penampungKey.get(penampungKey.size()-1) + " value akhir : " + penampungValue.get(penampungKey.size()-1));*/

            sendSms += penampungValue.get(0) + penampungValue.get(penampungKey.size()/2) + penampungValue.get(penampungKey.size()-1);
			/*sendSms += penampungValue.get(0) + penampungValue.get(penampungKey.size()/2) ;*/

        }

        return sendSms;
    }

    // delete data
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TempSmsLoc.TABLE,null,null);
        db.close();
    }

    //listdata
    public ArrayList<HashMap<String, String>> getData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TempSmsLoc.KEY_ID + "," +
                KEY_SMS +
                " FROM " + TempSmsLoc.TABLE;

        ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("sms", cursor.getString(cursor.getColumnIndex(KEY_SMS)));
                listData.add(data);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listData;
    }

    //=========================================================================================
    //=============================== PROSES DB TempDeliverySmsFailed ============================
    //=========================================================================================

    public int insert(TempDeliverySmsFailed tempDeliverySmsLoc){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TempDeliverySmsFailed.KEY_TIME_SMS, tempDeliverySmsLoc.time_sms);
        contentValues.put(TempDeliverySmsFailed.KEY_SMS_INTERVAL, tempDeliverySmsLoc.sms_interval);
        contentValues.put(TempDeliverySmsFailed.KEY_STATUS_DELIVERY, tempDeliverySmsLoc.status_delivery);

        //insert row
        long id =  sqLiteDatabase.insert(TempDeliverySmsFailed.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return (int) id;
    }
}
