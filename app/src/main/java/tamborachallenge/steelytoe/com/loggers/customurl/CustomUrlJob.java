package tamborachallenge.steelytoe.com.loggers.customurl;

import android.util.Log;

import tamborachallenge.steelytoe.com.common.events.UploadEvents;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

//import org.greenrobot.eventbus.EventBus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dki on 28/02/17.
 */

public class CustomUrlJob extends Job{
    private static final String TAG = CustomUrlJob.class.getSimpleName();
    private String logUrl;
    private String basicAuthUser;
    private String basicAuthPassword;
    private UploadEvents.BaseUploadEvent callbackEvent;

    public CustomUrlJob(String logUrl, String basicAuthUser, String basicAuthPassword, UploadEvents.BaseUploadEvent callbackEvent ) {
        super(new Params(1).requireNetwork().persist());
        this.logUrl = logUrl;
        this.basicAuthPassword = basicAuthPassword;
        this.basicAuthUser = basicAuthUser;
        this.callbackEvent = callbackEvent;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG,"Sending to URL: " + logUrl);

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();

//        if(!Strings.isNullOrEmpty(basicAuthUser)){
//            okBuilder.authenticator(new Authenticator() {
//                @Override
//                public Request authenticate(Route route, Response response) throws IOException {
//                    String credential = Credentials.basic(basicAuthUser, basicAuthPassword);
//                    return response.request().newBuilder().header("Authorization", credential).build();
//                }
//            });
//        }
//
//        okBuilder.sslSocketFactory(Networks.getSocketFactory(AppSettings.getInstance()));

        OkHttpClient client = okBuilder.build();

        Request request = new Request.Builder().url(logUrl).build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            Log.d(TAG,"Success - response code " + response);
//            EventBus.getDefault().post(callbackEvent.succeeded());
        }
        else {
            Log.d(TAG,"Unexpected response code " + response );
//            EventBus.getDefault().post(callbackEvent.failed("Unexpected code " + response,new Throwable(response.body().string())));
        }

        response.body().close();

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
//        EventBus.getDefault().post(callbackEvent.failed("Could not send to custom URL", throwable));
        Log.d(TAG,"Could not send to custom URL", throwable);
        return true;
    }
}
