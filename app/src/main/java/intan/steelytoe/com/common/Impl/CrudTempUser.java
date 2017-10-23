package intan.steelytoe.com.common.Impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import intan.steelytoe.com.model.TempUser;
import intan.steelytoe.com.sqllite.DBHelper;

/**
 * Created by dki on 23/02/17.
 */

public class CrudTempUser {
    private DBHelper dbHelper;

    public CrudTempUser(Context context){
        dbHelper = new DBHelper(context);
    }

    public String insert(TempUser tempUser){
        //Open Connection
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TempUser.KEY_NAMA, tempUser.nama_user);
        contentValues.put(TempUser.KEY_EMAIL, tempUser.email_user);

        //insert row
        sqLiteDatabase.insert(TempUser.TABLE, null, contentValues);
        sqLiteDatabase.close();
        return tempUser.email_user;
    }

    public TempUser getUserByEmail(String email){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                TempUser.KEY_ID + "," +
                TempUser.KEY_NAMA + "," +
                TempUser.KEY_EMAIL +
                " FROM " + TempUser.TABLE
                + " WHERE " +
                TempUser.KEY_EMAIL + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        TempUser tempUser = new TempUser();
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(email) } );

        if (cursor.moveToFirst()) {
            do {
                tempUser.id =cursor.getInt(cursor.getColumnIndex(TempUser.KEY_ID));
                tempUser.nama_user =cursor.getString(cursor.getColumnIndex(TempUser.KEY_NAMA));
                tempUser.email_user =cursor.getString(cursor.getColumnIndex(TempUser.KEY_EMAIL));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tempUser;
    }


}
