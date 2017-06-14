package tamborachallenge.steelytoe.com.common.events;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import tamborachallenge.steelytoe.com.util.PrefManager;

/**
 * Created by fadlymunandar on 6/13/17.
 */

public class RecognitionReciverService extends IntentService {

    public static final String RECOGNITION_RECEIVER_ACTION = "com.receiver.recognition";
    private static final String TAG = RecognitionReciverService.class.getSimpleName();
    private Context context;
    private Intent intent;

    public static final String EXTRA_LOCATION_SERVICE = "com.start.stop.receiver";
    public static final String ACTION_RECOGNITION_FILTER_RECEIVER = "com.fadly.reciver.recognition";
    private PrefManager prefManager;

    public RecognitionReciverService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefManager = PrefManager.getInstance(this);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "ACTIVITY RECOGNITION RECEIVER CALLED");
        this.context = this.getApplicationContext();
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivity(result.getMostProbableActivity());
        }
    }

    @Override
    public void onDestroy() {
        //bindLocationService(false);
        super.onDestroy();
    }

    private void handleDetectedActivity(DetectedActivity mostProbableActivity) {
        intent = new Intent(context, ServiceBackground.class);
        int type = mostProbableActivity.getType();
        int confidence = mostProbableActivity.getConfidence();
        switch (type) {
            case DetectedActivity.STILL:
                    case DetectedActivity.UNKNOWN:
                        stopLocationService(confidence);
                        break;
                    default:
                        restartLocationService();
                        break;
        }
    }

    private void remindUser() {
        Log.d(TAG, "LOCATION SERVICE STOPPED");
    }

    private void stopLocationService(int confidence) {
        if (confidence >=70){
            int totalStillCount = prefManager.getStillFlagActivity();
            if (totalStillCount >= 3) {
                sendBroadCast(0);
            }else {
                prefManager.addStillFlagActivity(totalStillCount + 1);
            }
        }
    }

    private void restartLocationService() {
        prefManager.addStillFlagActivity(0);
        sendBroadCast(1);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void sendBroadCast(int flag) {
        Intent intent = new Intent();
        intent.setAction(ACTION_RECOGNITION_FILTER_RECEIVER);
        intent.putExtra(EXTRA_LOCATION_SERVICE, flag);
        sendBroadcast(intent);
    }

}
