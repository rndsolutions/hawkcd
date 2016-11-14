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

import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;

/**
 * Created by rado on 13.11.16.
 */
public class AuthorizationManager implements  IAuthorizationManager {
    private static final Logger LOGGER = Logger.getLogger(AuthorizationManager.class);

    @Override
    public boolean  isAuthorized(User user, WsContractDto contract) throws ClassNotFoundException {
        LOGGER.debug("param: "+ user.toString());
        LOGGER.debug("param: "+ contract.toString());

        String fullyQualifedName = String.format("%s.%s", contract.getPackageName(), contract.getClassName());
        Class aClass = Class.forName(fullyQualifedName);

        final List<io.hawkcd.model.payload.Permission> permissions = user.getPermissions();
        final PermissionType permissionType = user.getPermissionType();

        for(Method method : aClass.getMethods())
        {
            final Annotation[] annotations = method.getAnnotations();

            for (Annotation a :  annotations){
                if (a instanceof Authorization){

                    for (Permission permission : permissions) {
                        //if ( permission.get)
                        //for () to:do
                    }
                    //System.out.println(a.toString());
                    //System.out.println(method.toString());
                }
            }
        }

        //contract.getClassName();

        return true;
    }

    @Override
    public void getAllUsersWithPermissions() {

    }
}
