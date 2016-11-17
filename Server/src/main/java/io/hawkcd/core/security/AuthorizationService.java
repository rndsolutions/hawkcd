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

import io.hawkcd.model.*;
import io.hawkcd.model.payload.Permission;

import java.util.ArrayList;
import java.util.List;


/**
 * The class is responsible for authorizing User requests
 * Workflow:
 *
 */
public class AuthorizationService {

    /**
     * The method extracts the object Ids necessary for the authorization based on the class and method being invoked.
     * @param className
     * @param methodName
     * @param arguments
     * @param userPermissions
     * @param authorizedPermission
     * @return
     */
    boolean isRequestAuthorized(String className, String methodName, List<Object> arguments, UserPermissions userPermissions, Authorization authorizedPermission) {
        boolean hasPermission;
        if (methodName.startsWith("getAll")) { // If no arguments are passed
            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission);
        } else if (methodName.startsWith("delete") || methodName.startsWith("getById") || methodName.startsWith("unassign")) { // If one String is passed as argument
            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, arguments.get(0).toString());
        } else if (methodName.startsWith("assign")) { // If two Strings are passed as arguments
            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, arguments.get(0).toString());
            if (hasPermission) {
                hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, arguments.get(1).toString());
            }
        } else {
            List<String> listOfIds = new ArrayList<>();
            switch (className) { // If an Object is passed as argument
                case "PipelineGroupService":
                    PipelineGroup pipelineGroup = (PipelineGroup) arguments.get(0);
                    listOfIds.add(pipelineGroup.getId());
                    break;
                case "PipelineDefinitionService":
                    PipelineDefinition pipelineDefinition = (PipelineDefinition) arguments.get(0);
                    listOfIds.add(pipelineDefinition.getId());
                    listOfIds.add(pipelineDefinition.getPipelineGroupId());
                    break;
                case "StageDefinitionService":
                    StageDefinition stageDefinition = (StageDefinition) arguments.get(0);
                    listOfIds.add(stageDefinition.getPipelineDefinitionId());
//                    entityIds.add(stageDefinition.getPipelineGroupId());
                    break;
                case "JobDefinitionService":
                    JobDefinition jobDefinition = (JobDefinition) arguments.get(0);
                    listOfIds.add(jobDefinition.getPipelineDefinitionId());
//                    entityIds.add(jobDefinition.getPipelineGroupId());
                    break;
                case "TaskDefinitionService":
                    TaskDefinition taskDefinition = (TaskDefinition) arguments.get(0);
                    listOfIds.add(taskDefinition.getPipelineDefinitionId());
//                    entityIds.add(taskDefinition.getPipelineGroupId());
                    break;
                case "PipelineService":
                    Pipeline pipeline = (Pipeline) arguments.get(0);
                    listOfIds.add(pipeline.getPipelineDefinitionId());
                    break;
            }

            String[] entityIds = listOfIds.toArray(new String[listOfIds.size()]);
            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, entityIds);
        }

        return hasPermission;
    }


    /**
     * The method tries to authorize against the User's Pipeline specific Permissions.
     * If none match the passed entityIds, tries to authorize against the User's PipelineGroup specific Permissions.
     * If none match the passed entityIds, tries to authorize against the User's generic Permissions.
     * @param userPermissions
     * @param authorizedPermission
     * @param entityIds
     * @return
     */
    boolean hasPermissionWithId(UserPermissions userPermissions, Authorization authorizedPermission, String... entityIds) {
        Boolean hasPermission;
        for (String entityId : entityIds) {
            hasPermission = this.hasPermissionForSpecificEntity(userPermissions.getPipelineSpecific(), authorizedPermission, entityId);
            if (hasPermission != null) {
                return hasPermission;
            }

            hasPermission = this.hasPermissionForSpecificEntity(userPermissions.getPipelineGroupSpecific(), authorizedPermission, entityId);
            if (hasPermission != null) {
                return hasPermission;
            }
        }

        hasPermission = this.hasPermissionForGenericEntity(userPermissions.getGeneric(), authorizedPermission);

        return hasPermission;
    }


    /**
     * The method authorizes a User against his/her specific Permissions
     * @param userPermissions
     * @param authorizedPermission
     * @param entityId
     * @return
     */
    Boolean hasPermissionForSpecificEntity(List<Permission> userPermissions, Authorization authorizedPermission, String entityId) {
        Boolean hasPermission = null;
        for (Permission permission : userPermissions) {
            if (permission.getPermittedEntityId().equals(entityId)) {
                hasPermission = this.hasPermission(permission, authorizedPermission);
                break;
            }
        }

        return hasPermission;
    }

    /**
     * The method authorizes a User against his/her generic Permissions
     * @param userPermissions
     * @param authorizedPermission
     * @return
     */
    boolean hasPermissionForGenericEntity(List<Permission> userPermissions, Authorization authorizedPermission) {
        boolean hasPermission = false;
        for (Permission permission : userPermissions) {
            hasPermission = this.hasPermission(permission, authorizedPermission);
            if (hasPermission) {
                break;
            }
        }

        return hasPermission;
    }

    /**
     * The method checks a Permission against the minimum required by a method
     * @param userPermission
     * @param authorizedPermission
     * @return
     */
    boolean hasPermission(Permission userPermission, Authorization authorizedPermission) {
        if (userPermission.getPermissionType().getPriorityLevel() >= authorizedPermission.type().getPriorityLevel()) {
            if (userPermission.getPermissionScope().getPriorityLevel() >= authorizedPermission.scope().getPriorityLevel()) {
                return true;
            }
        }

        return false;
    }
}
