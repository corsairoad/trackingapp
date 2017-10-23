package intan.steelytoe.com.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by haiv on 22/03/17.
 */

public class TempDeliverySmsFailed extends ArrayList<HashMap<String, String>> {
    // Labels table name
    public static final String TABLE = "temp_deliverysmsloc";

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

    public int getId() {
        return id;
    }

}
