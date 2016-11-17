/*
 *   Copyright (C) 2016 R&D Solutions Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package io.hawkcd.core.security;

import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The class is responisble for authorizing User's requests
 * <p>
 * Workflow:
 * Evaluate currentUser permissions,
 * if method call is allowed
 * Get all active sessions from database (cluster)
 * Filter all users with active session
 * Evaluate each user permissions and prepare the message object to be broadcast
 * Send message to all subscribers
 */
public class AuthorizationManager implements IAuthorizationManager {
    private static final Logger LOGGER = Logger.getLogger(AuthorizationManager.class);

    private AuthorizationService authorizationService;


    @Override
    public boolean isAuthorized(User user, WsContractDto contract, List<Object> parameters) throws ClassNotFoundException, NoSuchMethodException {
        LOGGER.debug("param: " + user.toString());
        LOGGER.debug("param: " + contract.toString());

        Authorization authorizationPermission = this.getMethodAuthorization(contract.getPackageName(), contract.getClassName(), contract.getMethodName());
        if (authorizationPermission == null) {
            return false;
        }

        boolean isAuthorized = this.authorizationService.isRequestAuthorized(contract.getClassName(), contract.getMethodName(), parameters, user.getUserPermissions(), authorizationPermission);

        return isAuthorized;
    }

    @Override
    public void getAllUsersWithPermissions() {

    }

    Authorization getMethodAuthorization(String packageName, String className, String methodName)
            throws ClassNotFoundException, NoSuchMethodException {

        String fullyQualifiedName = String.format("%s.%s", packageName, className);
        Class<?> aClass = Class.forName(fullyQualifiedName);
        Method method = aClass.getMethod(methodName);
        Authorization annotation = method.getAnnotation(Authorization.class);

        return annotation;
    }
}
