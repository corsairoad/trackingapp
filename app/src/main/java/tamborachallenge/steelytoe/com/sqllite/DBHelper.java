package tamborachallenge.steelytoe.com.sqllite;

/**
 * Created by IT001 on 23-Jun-16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tamborachallenge.steelytoe.com.model.TempDeliverySmsFailed;
import tamborachallenge.steelytoe.com.model.TempDeliverySmsSent;
import tamborachallenge.steelytoe.com.model.TempLoaction;
import tamborachallenge.steelytoe.com.model.TempSession;
import tamborachallenge.steelytoe.com.model.TempSmsLoc;
import tamborachallenge.steelytoe.com.model.TempUser;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "gts06.db";


    /**
     * StrukTur Create Table
     *
     * Create berada di model
     *
     *  ->Table Name
     *      ->Colom
     *
     */

    private static final String CREATE_TABLE_TEMP_USER = "CREATE TABLE " + TempUser.TABLE  + "("
            + TempUser.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + TempUser.KEY_NAMA + " TEXT , "
            + TempUser.KEY_EMAIL + " TEXT )";

    private static final String CREATE_TABLE_TEMP_LOCATION = "CREATE TABLE " + TempLoaction.TABLE  + "("
            + TempLoaction.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + TempLoaction.KEY_lat + " TEXT , "
            + TempLoaction.KEY_lng + " TEXT , "
            + TempLoaction.KEY_timer + " TEXT )";

    private static final String CREATE_TABLE_TEMP_SESSION = "CREATE TABLE " + TempSession.TABLE  + "("
            + TempSession.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + TempSession.KEY_STATUS + " TEXT )";

    private static final String CREATE_TABLE_TEMP_SMS_LOC = "CREATE TABLE " + TempSmsLoc.TABLE  + "("
            + TempSmsLoc.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + TempSmsLoc.KEY_SMS + " TEXT )";

    private static final String CREATE_TABLE_TEMP_DELIVERY_SMS_LOC = "CREATE TABLE " + TempDeliverySmsFailed.TABLE  + "("
            + TempDeliverySmsFailed.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + TempDeliverySmsFailed.KEY_TIME_SMS + " TEXT , "
            + TempDeliverySmsFailed.KEY_SMS_INTERVAL + " TEXT , "
            + TempDeliverySmsFailed.KEY_STATUS_DELIVERY + " TEXT )";

    private static final String CREATE_TABLE_TEMP_DELIVERY_SMS_SENT = "CREATE TABLE " + TempDeliverySmsSent.TABLE  + "("
            + TempDeliverySmsSent.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + TempDeliverySmsSent.KEY_TIME_SMS + " TEXT , "
            + TempDeliverySmsSent.KEY_SMS_INTERVAL + " TEXT , "
            + TempDeliverySmsSent.KEY_STATUS_DELIVERY + " TEXT )";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TEMP_USER);
        db.execSQL(CREATE_TABLE_TEMP_LOCATION);
        db.execSQL(CREATE_TABLE_TEMP_SESSION);
        db.execSQL(CREATE_TABLE_TEMP_SMS_LOC);
        db.execSQL(CREATE_TABLE_TEMP_DELIVERY_SMS_LOC);
        db.execSQL(CREATE_TABLE_TEMP_DELIVERY_SMS_SENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + TempUser.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TempLoaction.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TempSession.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TempSmsLoc.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TempDeliverySmsFailed.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TempDeliverySmsSent.TABLE);

        // Create tables again#
        onCreate(db);

    }

}