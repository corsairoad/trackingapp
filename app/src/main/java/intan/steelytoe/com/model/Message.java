package intan.steelytoe.com.model;

import android.location.Location;

/**
 * Created by DIGIKOM-EX4 on 2/14/2017.
 */

public class Message {

    /// ££££££££££££ Like openGTS
    public static class LocationUpdate{
        public Location location;
        public String timer;
        public LocationUpdate(Location loc, String timer) {
            this.location = loc;
            this.timer = timer;
        }

        public Location getLocation() {
            return location;
        }
    }

}
