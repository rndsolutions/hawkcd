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
import io.hawkcd.model.payload.Permission;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;

public class AuthorizationManager implements IAuthorizationManager {
    private static final Logger LOGGER = Logger.getLogger(AuthorizationManager.class);

    @Override
    public boolean isAuthorized(User user, WsContractDto contract) throws ClassNotFoundException, NoSuchMethodException {
        LOGGER.debug("param: " + user.toString());
        LOGGER.debug("param: " + contract.toString());

        Authorization authorization = this.getMethodAuthorizationAnnotation(contract.getPackageName(), contract.getClassName(), contract.getMethodName());
        if (authorization == null) {
            return false;
        }

        


        final List<Permission> permissions = user.getPermissions();



        return true;
    }

    @Override
    public void getAllUsersWithPermissions() {

    }

    private Authorization getMethodAuthorizationAnnotation(String packageName, String className, String methodName)
            throws ClassNotFoundException, NoSuchMethodException {

        String fullyQualifiedName = String.format("%s.%s", packageName, className);
        Class<?> aClass = Class.forName(fullyQualifiedName);
        Method method = aClass.getMethod(methodName);
        Authorization annotation = method.getAnnotation(Authorization.class);

        return annotation;
    }
}
