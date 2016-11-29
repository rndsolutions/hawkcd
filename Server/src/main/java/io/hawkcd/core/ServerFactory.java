package io.hawkcd.core;

import io.hawkcd.services.ServerService;

/**
 * Created by rado on 28.11.16.
 */
public class ServerFactory {

    public static ServerService getServerService(){
        return  ServerService.getInstance();
    }
}
