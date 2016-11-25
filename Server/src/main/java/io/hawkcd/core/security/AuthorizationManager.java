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
import io.hawkcd.model.enums.PermissionType;
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

    private AuthorizationService authorizationService = new AuthorizationService();


    @Override
    public boolean isAuthorized(User user, WsContractDto contract, List<Object> parameters) throws ClassNotFoundException, NoSuchMethodException {
        Authorization authorizationAttributes = this.getMethodAuthorizationAttributes(contract.getPackageName(), contract.getClassName(), contract.getMethodName(), parameters);
        if (authorizationAttributes == null) {
            return false;
        }

        String[] entityIds = this.authorizationService.extractEntityIds(parameters);
//        String[] entityIds = this.authorizationService.extractEntityIds(contract.getClassName(), contract.getMethodName(), parameters, user.getUserPermissions(), authorizationAttributes);
        Grant grant = new Grant(authorizationAttributes);

        PermissionType permissionType = this.authorizationService.determinePermissionTypeForUser(user.getPermissions(), grant, entityIds);
        if (permissionType != PermissionType.NONE) {
            return true;
        }
        return false;
    }

    @Override
    public void getAllUsersWithPermissions() {
    }

    /**
     * Checks the object for minimum permissions required for the user to receive the object
     *
     * @param userGrants
     * @param object
     * @return
     */
    public PermissionType determinePermissionTypeForEntity(List<Grant> userGrants, Object object, List<Object> parameters) {
        PermissionType result;
        Authorization authorization = object.getClass().getAnnotation(Authorization.class);
        Grant grant = new Grant(authorization);

        String[] entityIds = this.authorizationService.extractEntityIds(parameters);
        result = this.authorizationService.determinePermissionTypeForUser(userGrants, grant, entityIds);
        return result;
    }

    private Authorization getMethodAuthorizationAttributes(String packageName, String className, String methodName, List<Object> parameters)
            throws ClassNotFoundException, NoSuchMethodException {

        String fullyQualifiedName = String.format("%s.%s", packageName, className);
        Class<?> aClass = Class.forName(fullyQualifiedName);
        Class<?>[] params = new Class[parameters.size()];
        for (int i = 0; i < params.length; i++) {
            Class<?> aClass1 = parameters.get(i).getClass();
            params[i] = aClass1;
        }
        Method method = aClass.getMethod(methodName, params);
        Authorization annotation = method.getAnnotation(Authorization.class);

        return annotation;
    }

}
