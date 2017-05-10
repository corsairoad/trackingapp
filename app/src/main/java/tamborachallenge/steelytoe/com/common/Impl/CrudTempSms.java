package tamborachallenge.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tamborachallenge.steelytoe.com.model.TempSmsLoc;
import tamborachallenge.steelytoe.com.model.TempUser;
import tamborachallenge.steelytoe.com.sqllite.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static tamborachallenge.steelytoe.com.model.TempSmsLoc.KEY_SMS;

/**
 * Created by dki on 02/03/17.
 */

public class CrudTempSms {
    private DBHelper dbHelper;

    public CrudTempSms (Context context){
        dbHelper = new DBHelper(context);
    }

    public int insert(TempSmsLoc tempSmsLoc){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TempSmsLoc.KEY_SMS, tempSmsLoc.sms);

        //insert row
        long id =  sqLiteDatabase.insert(TempUser.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return (int) id;
    }



    //check count row
    public int getRowCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TempSmsLoc.KEY_ID + "," +
                KEY_SMS +
                " FROM " + TempSmsLoc.TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
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
                data.put("id", cursor.getString(cursor.getColumnIndex(TempSmsLoc.KEY_ID)));
                data.put("sms", cursor.getString(cursor.getColumnIndex(KEY_SMS)));
                listData.add(data);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listData;
    }
}
