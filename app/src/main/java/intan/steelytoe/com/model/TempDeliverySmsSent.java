package intan.steelytoe.com.model;

/**
 * Created by haiv on 03/04/17.
 */

public class TempDeliverySmsSent {
    // Labels table name
    public static final String TABLE = "temp_deliverysmssent";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_TIME_SMS = "time_sms";
    public static final String KEY_SMS_INTERVAL = "sms_interval";
    public static final String KEY_STATUS_DELIVERY = "status_delivery";

    // property help us to keep data
    public int id;
    public String time_sms;
    public String sms_interval;
    public String status_delivery;
}
