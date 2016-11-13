package io.hawkcd.core.session;

/**
 * Created by rado on 13.11.16.
 */
public class SessionFactory {

    /*
    * Returns a new instance of the SessionManager object
    */
    public static  ISessionManager getSessionManager() {
        return new SessionManager();
    }
}
