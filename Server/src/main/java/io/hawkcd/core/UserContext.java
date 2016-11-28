package io.hawkcd.core;

import io.hawkcd.ws.WSSocket;

/**
 * Created by rado on 11.11.16.
 * <p>
 * Represents the application context
 */
public class UserContext {

    /**
     * Gives an access to the current user session
     */
    private WSSocket session;


    public UserContext(WSSocket session) {

        this.session = session;
    }

    public WSSocket getSession() {
        return this.session;
    }

}
