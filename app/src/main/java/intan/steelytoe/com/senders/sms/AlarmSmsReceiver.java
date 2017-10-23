package intan.steelytoe.com.senders.sms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import intan.steelytoe.com.model.TempDeliverySmsFailed;
import intan.steelytoe.com.model.TempSmsLoc;
import intan.steelytoe.com.sqllite.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static intan.steelytoe.com.model.TempSmsLoc.KEY_SMS;

public class AlarmSmsReceiver extends BroadcastReceiver {
	private static final String TAG = AlarmSmsReceiver.class.getSimpleName();
	private DBHelper dbHelper;
	SmsManager smsManager = SmsManager.getDefault();


        @Override
        public void onReceive (Context arg0, Intent arg1){
            dbHelper = new DBHelper(arg0);
            String sts_delivery = null;

            Toast.makeText(arg0, "Send Sms ", Toast.LENGTH_SHORT).show();

            Time now = new Time();
            now.setToNow();
            String timeOfEvent = now.format("%H:%M:%S");

		    /*Check Phone Nomber*/
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(arg0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String numberDestination = sharedPreferences.getString("sms_server_number", null); // getting String
            if (numberDestination == null || numberDestination == "") {
                editor.putString("sms_server_number", "082211122203");
                editor.commit();
                numberDestination = "082211122203";
            }

            String rowCont = getRowCount();
            if (rowCont != null) {
                smsManager.sendTextMessage(numberDestination, null, rowCont, null, null); // 085730748514  == 082211122203
                deleteAll();
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(arg0, "SMS sent", Toast.LENGTH_LONG).show();
                        sts_delivery = "Sukses";
                        insertToSQLiteDatabase(timeOfEvent, rowCont, sts_delivery);
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(arg0, "Sukses", Toast.LENGTH_LONG).show();
                        sts_delivery = "Sukses";
                        insertToSQLiteDatabase(timeOfEvent, rowCont, sts_delivery);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(arg0, "RESULT_ERROR_GENERIC_FAILURE", Toast.LENGTH_LONG).show();
                        sts_delivery = "Generic failure";
                        insertToSQLiteDatabase(timeOfEvent, rowCont, sts_delivery);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(arg0, "RESULT_ERROR_NO_SERVICE", Toast.LENGTH_LONG).show();
                        sts_delivery = "No service";
                        insertToSQLiteDatabase(timeOfEvent, rowCont, sts_delivery);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(arg0, "RESULT_ERROR_NULL_PDU", Toast.LENGTH_LONG).show();
                        sts_delivery = "Null PDU";
                        insertToSQLiteDatabase(timeOfEvent, rowCont, sts_delivery);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(arg0, "RESULT_ERROR_RADIO_OFF", Toast.LENGTH_LONG).show();
                        sts_delivery = "Radio off";
                        insertToSQLiteDatabase(timeOfEvent, rowCont, sts_delivery);
                        break;
                }
                Log.d("dataSMS ", "" + timeOfEvent + rowCont);
            }
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

	// ================================================================================== Insert To Database
	private final void insertToSQLiteDatabase(String timeOfEvent, String sendSms, String sts_delivery){
		int _Id=0;

		TempDeliverySmsFailed tempDeliverySmsLoc = new TempDeliverySmsFailed();
		tempDeliverySmsLoc.time_sms = timeOfEvent;
		tempDeliverySmsLoc.sms_interval = sendSms;
		tempDeliverySmsLoc.status_delivery = sts_delivery;
		tempDeliverySmsLoc.id=_Id;

		if (_Id == 0 ){
			_Id = insert(tempDeliverySmsLoc);
			Log.d(TAG, "Input Temp Delivery " + _Id);
		}

	}

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
