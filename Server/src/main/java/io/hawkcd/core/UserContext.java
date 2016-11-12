package io.hawkcd.core;

import io.hawkcd.ws.WSSession;

/**
 * Created by rado on 11.11.16.
 *
 * Represents the application context
 */
public class UserContext {

    /**
     * Gives an access to the current user session
     */
    private WSSession session;


    public  UserContext(WSSession session){

        this.session= session;
    }

    public  WSSession getSession(){
        return this.session;
    }

}
