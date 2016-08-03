package net.hawkengine.core.utilities.securityFilters;

import java.io.IOException;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public class AuthenticationFilter implements ContainerRequestFilter {

    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String resourceClass = this.resourceInfo.getResourceClass().getName();
        String methodName = this.resourceInfo.getResourceMethod().getName();
        System.out.println("REST invocation bound to resource class: " + resourceClass);
        System.out.println("REST invocation bound to resource method: " + methodName);

    }
}
    /*
        // Get the HTTP Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {

            // Validate the token
            validateToken(token);

        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }

    }

    private void validateToken(String token) throws Exception {

        // Check if it was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
    }
}
*/