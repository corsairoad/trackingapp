package tamborachallenge.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import tamborachallenge.steelytoe.com.model.TempDeliverySmsSent;
import tamborachallenge.steelytoe.com.sqllite.DBHelper;

/**
 * Created by haiv on 22/03/17.
 */

public class CrudTempDeliverySmsSent {
    private DBHelper dbHelper;

    public CrudTempDeliverySmsSent(Context context){
        dbHelper = new DBHelper(context);
    }


    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TempDeliverySmsSent.TABLE,null,null);
        db.close(); // Closing database connection
    }

    public int insert(TempDeliverySmsSent tempDeliverySmsSent){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TempDeliverySmsSent.KEY_TIME_SMS, tempDeliverySmsSent.time_sms);
        contentValues.put(TempDeliverySmsSent.KEY_SMS_INTERVAL, tempDeliverySmsSent.sms_interval);
        contentValues.put(TempDeliverySmsSent.KEY_STATUS_DELIVERY, tempDeliverySmsSent.status_delivery);

        //insert row
        long id =  sqLiteDatabase.insert(TempDeliverySmsSent.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return (int) id;
    }
}
