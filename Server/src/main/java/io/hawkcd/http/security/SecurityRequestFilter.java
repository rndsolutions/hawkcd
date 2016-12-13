/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.http.security;

import io.hawkcd.model.payload.TokenInfo;
import io.hawkcd.utilities.deserializers.TokenAdapter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
@Secured
public class SecurityRequestFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MultivaluedMap<String, String> pathParameters = requestContext.getUriInfo().getQueryParameters();
        //requestContext.
        //TODO: Consider checking the user against the database
        String api_key = pathParameters.get("api_key").stream().findFirst().orElse(null);
        TokenInfo tokenInfo = null;
        if (api_key != null) {
            tokenInfo = TokenAdapter.verifyToken(api_key);
        }

        HttpSecurityContext httpSecurityContext = null;
        try {
            //sets app specific SecurityContext
            httpSecurityContext = new HttpSecurityContext(tokenInfo.getUser(), this.resourceInfo);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        requestContext.setProperty("user", tokenInfo.getUser());
        requestContext.setSecurityContext(httpSecurityContext);

        // we may stop the processing here
        try {
            if (!httpSecurityContext.getMethodName().equals("logout") && !httpSecurityContext.isAuthorized()) { // if true, the user is not authorized thus stop the request processing
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}