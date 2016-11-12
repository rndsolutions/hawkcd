package io.hawkcd.core.subscriber;

import org.apache.log4j.Logger;

import io.hawkcd.core.Message;

/**
 * Created by rado on 11.11.16.
 */
public class AuthFilter implements IMessageFilter {

    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getClass());

    public  AuthFilter(){

    }

    @Override
    public void applyFilter(Message message) {
        LOGGER.info("authFilter message:"+ message);
    }
}
