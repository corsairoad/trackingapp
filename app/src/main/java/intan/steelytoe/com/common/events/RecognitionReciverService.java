package intan.steelytoe.com.common.events;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import intan.steelytoe.com.util.PrefManager;

/**
 * Created by fadlymunandar on 6/13/17.
 */

public class RecognitionReciverService extends IntentService {

    public static final String RECOGNITION_RECEIVER_ACTION = "com.receiver.recognition";
    private static final String TAG = RecognitionReciverService.class.getSimpleName();
    private Context context;
    private Intent intent;

    public static final String EXTRA_LOCATION_SERVICE = "com.start.stop.receiver";
    public static final String ACTION_RECOGNITION_FILTER_RECEIVER = "com.reciver.recognition";
    public static final String ACTION_ACTIVITY_MOVEMENT = "com.receiver.activity.movement";
    public static final String EXTRA_ACTIVITY_MOVEMENT = "com.receiver.extra.movement";
    public static final int FLAG_STOP_LOCATION_SERVICE = 0;
    public static final int FLAG_START_LOCATION_SERVICE = 1;

    private static final String ACTIVITY_STILL = "STILL";
    private static final String ACTIVITY_WALKING = "WALKING";
    private static final String ACTIVITY_RUNNING = "RUNNING";


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
                        sendActivityMovementBroadcast(ACTIVITY_STILL);
                        stopLocationService(confidence);
                        break;
                    case DetectedActivity.IN_VEHICLE:
                        case DetectedActivity.ON_BICYCLE:
                            case DetectedActivity.ON_FOOT:
                                case DetectedActivity.TILTING:
                                    case DetectedActivity.RUNNING:
                                        sendActivityMovementBroadcast(ACTIVITY_RUNNING);
                                        restartLocationService();
                                        break;
                                    case DetectedActivity.WALKING:
                                        sendActivityMovementBroadcast(ACTIVITY_WALKING);
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
            Log.d(TAG, "TOTAL STILL COUNT: " + totalStillCount);
            if (totalStillCount >= 15) {
                sendActRecogBroadCast(FLAG_STOP_LOCATION_SERVICE);
                prefManager.addStillFlagActivity(0);
            }else {
                prefManager.addStillFlagActivity(totalStillCount + 1);
            }
        }
    }

    private void restartLocationService() {
        prefManager.addStillFlagActivity(0);
        sendActRecogBroadCast(FLAG_START_LOCATION_SERVICE);
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

    private void sendActRecogBroadCast(int flag) {
        Intent intent = new Intent();
        intent.setAction(ACTION_RECOGNITION_FILTER_RECEIVER);
        intent.putExtra(EXTRA_LOCATION_SERVICE, flag);
        sendBroadcast(intent); // RECEIVER IS IN THE RecognitionService.class
    }

    private void sendActivityMovementBroadcast(String movement) {
        Intent intent = new Intent(ACTION_ACTIVITY_MOVEMENT);
        intent.putExtra(EXTRA_ACTIVITY_MOVEMENT, movement);
        sendBroadcast(intent);
    }

}
