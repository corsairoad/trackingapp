package intan.steelytoe.com.loggers.opengts;

import android.content.Context;
import android.location.Location;

import intan.steelytoe.com.loggers.FileLogger;
import intan.steelytoe.com.common.SerializableLocation;
import intan.steelytoe.com.senders.opengts.OpenGTSManager;

/**
 * Created by dki on 27/02/17.
 */

public class OpenGTSLogger implements FileLogger {
    protected final String name = "OpenGTS";
    final Context context;

    public OpenGTSLogger(Context context) {
        this.context = context;
    }

    @Override
    public void write(Location loc) throws Exception {
        OpenGTSManager manager = new OpenGTSManager(context);
        manager.sendLocations(new SerializableLocation[]{new SerializableLocation(loc)}, context.getApplicationContext());
    }


}
