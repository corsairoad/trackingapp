package tamborachallenge.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tamborachallenge.steelytoe.com.model.TempSession;
import tamborachallenge.steelytoe.com.sqllite.DBHelper;

/**
 * Created by dki on 2/20/17.
 */

public class CrudTempSession {
    private DBHelper dbHelper;

    public CrudTempSession(Context context){
        dbHelper = new DBHelper(context);
    }

    public int insert(TempSession tempSession){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TempSession.KEY_STATUS, tempSession.sts);

        //insert row
        long id = sqLiteDatabase.insert(TempSession.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return (int) id;
    }

    public TempSession getFirstRow(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT * FROM '" + TempSession.TABLE + "'";

        TempSession tempSession = new TempSession();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()) {
            tempSession.id =cursor.getInt(cursor.getColumnIndex(TempSession.KEY_ID));
            tempSession.sts =cursor.getInt(cursor.getColumnIndex(TempSession.KEY_STATUS));
        }

        cursor.close();
        db.close();
        return tempSession;
    }

    public TempSession getSessionById(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TempSession.KEY_ID + "," +
                TempSession.KEY_STATUS +
                " FROM " + TempSession.TABLE
                + " WHERE " +
                TempSession.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        TempSession tempSession = new TempSession();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                tempSession.id =cursor.getInt(cursor.getColumnIndex(TempSession.KEY_ID));
                tempSession.sts =cursor.getInt(cursor.getColumnIndex(TempSession.KEY_STATUS));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tempSession;
    }

    public void update(TempSession tempSession){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(tempSession.KEY_STATUS, tempSession.sts);

        db.update(TempSession.TABLE, values, TempSession.KEY_ID + "= ?", new String[]
                { String.valueOf(tempSession.id)});
        db.close();
    }

    public void deleteAll() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TempSession.TABLE,null,null);
        db.close(); // Closing database connection
    }



}
