package io.hawkcd.http.security;

import io.hawkcd.core.security.AuthorizationFactory;
import io.hawkcd.model.Entity;
import io.hawkcd.model.User;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

/**
 * Created by rado on 07.12.16.
 */
@Provider
public class SecurityResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if(responseContext.getEntity() instanceof List){
            List<Entity> filteredEntities = AuthorizationFactory.getAuthorizationManager().filterResponse((List<Entity>) responseContext.getEntity(), (User) requestContext.getProperty("user"));
            responseContext.setEntity(filteredEntities);
        }
    }
}
