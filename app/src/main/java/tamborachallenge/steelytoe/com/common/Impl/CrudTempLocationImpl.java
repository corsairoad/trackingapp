package tamborachallenge.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tamborachallenge.steelytoe.com.model.TempLoaction;
import tamborachallenge.steelytoe.com.sqllite.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class CrudTempLocationImpl {
    private DBHelper dbHelper;

    public CrudTempLocationImpl(Context context){
        dbHelper = new DBHelper(context);
    }

    public int insert(TempLoaction tempLoaction){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TempLoaction.KEY_lat, tempLoaction.lat);
        contentValues.put(TempLoaction.KEY_lng, tempLoaction.lng);
        contentValues.put(TempLoaction.KEY_timer, tempLoaction.timer);

        //insert row
        long id = sqLiteDatabase.insert(TempLoaction.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return (int) id;
    }
	

    public void deleteAll() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TempLoaction.TABLE,null,null);
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getLocation() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TempLoaction.KEY_ID + "," +
                TempLoaction.KEY_lat + "," +
                TempLoaction.KEY_lng + "," +
                TempLoaction.KEY_timer +
                " FROM " + TempLoaction.TABLE;

        //Student student = new Student();
        ArrayList<HashMap<String, String>> locationList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> location = new HashMap<String, String>();
                location.put("id", cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_ID)));
                location.put("lat", cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lat)));
                location.put("lng", cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lng)));
                location.put("timer", cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_timer)));
                locationList.add(location);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return locationList;

    }

    public TempLoaction getLocationById(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TempLoaction.KEY_ID + "," +
                TempLoaction.KEY_lat+ "," +
                TempLoaction.KEY_lng + "," +
                TempLoaction.KEY_timer +
                " FROM " + TempLoaction.TABLE
                + " WHERE " +
                TempLoaction.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        TempLoaction tempLoaction = new TempLoaction();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                tempLoaction.id =cursor.getInt(cursor.getColumnIndex(TempLoaction.KEY_ID));
                tempLoaction.lat =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lat));
                tempLoaction.lng  =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lng));
                tempLoaction.timer =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_timer));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tempLoaction;
    }

    public TempLoaction getFirstRow(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM '" + TempLoaction.TABLE + "'";

        TempLoaction tempLoaction = new TempLoaction();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            tempLoaction.id =cursor.getInt(cursor.getColumnIndex(TempLoaction.KEY_ID));
            tempLoaction.lat =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lat));
            tempLoaction.lng  =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lng));
            tempLoaction.timer =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_timer));
        }

        cursor.close();
        db.close();
        return tempLoaction;
    }

    public TempLoaction getLastRow(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM '" + TempLoaction.TABLE + "'";

        TempLoaction tempLoaction = new TempLoaction();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToLast()) {
            tempLoaction.id =cursor.getInt(cursor.getColumnIndex(TempLoaction.KEY_ID));
            tempLoaction.lat =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lat));
            tempLoaction.lng  =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_lng));
            tempLoaction.timer =cursor.getString(cursor.getColumnIndex(TempLoaction.KEY_timer));
        }

        cursor.close();
        db.close();
        return tempLoaction;
    }

}
