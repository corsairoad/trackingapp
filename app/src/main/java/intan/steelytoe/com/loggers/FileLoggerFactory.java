package intan.steelytoe.com.loggers;

import android.content.Context;
import android.location.Location;

import intan.steelytoe.com.loggers.opengts.OpenGTSLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dki on 27/02/17.
 */

public class FileLoggerFactory {
    public static List<FileLogger> getFileLoggers(Context context) {

        List<FileLogger> loggers = new ArrayList<>();

        loggers.add(new OpenGTSLogger(context));

        return loggers;
    }


    public static void write(Context context, Location loc) throws Exception {
        for (FileLogger logger : getFileLoggers(context)) {
            logger.write(loc);
        }
    }

}
