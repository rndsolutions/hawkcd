package io.hawkcd.core.security;

/**
 * Created by rado on 14.11.16.
 */
public class AuthorizationFactory {

    public static IAuthorizationManager getAuthorizationManager(){
        return new AuthorizationManager();
    }

}
