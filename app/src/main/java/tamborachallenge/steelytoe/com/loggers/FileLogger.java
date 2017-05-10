package tamborachallenge.steelytoe.com.loggers;

import android.location.Location;

/**
 * Created by dki on 27/02/17.
 */

public interface FileLogger {
    void write(Location loc) throws Exception;
}
