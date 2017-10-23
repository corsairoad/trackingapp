package intan.steelytoe.com.common.events;

/**
 * Created by dki on 28/02/17.
 */

public class UploadEvents {
    @SuppressWarnings("unchecked")
    public static abstract class BaseUploadEvent implements java.io.Serializable{
        public boolean success;
        public String message;
        public Throwable throwable;


        /**
         * Convenience function, returns a succeeded event
         */
        public <T extends BaseUploadEvent> T succeeded(){
            this.success = true;
            return (T) this;
        }

        /**
         * Convenience function, returns a succes event with a message
         */
        public <T extends BaseUploadEvent> T succeeded(String message){
            this.success = true;
            this.message = message;
            return (T) this;
        }


        /**
         * Convenience function, returns a failed event
         */
        public <T extends BaseUploadEvent> T failed(){
            this.success = false;
            this.message = null;
            this.throwable = null;
            return (T)this;
        }

        /**
         * Convenience function, returns a failed event with just a message
         */
        public <T extends BaseUploadEvent> T failed(String message){
            this.success = false;
            this.message = message;
            this.throwable = null;
            return (T)this;
        }

        /**
         * Convenience function, returns a failed event with a message and a throwable
         */
        public <T extends BaseUploadEvent> T failed(String message, Throwable throwable){
            this.success = false;
            this.message = message;
            this.throwable = throwable;
            return (T)this;
        }
    }

    public static class OpenGTS extends BaseUploadEvent {}


}
