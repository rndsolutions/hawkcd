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
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.model.payload.Permission;

import java.util.ArrayList;
import java.util.List;


/**
 * The class is responsible for authorizing User requests
 * Workflow:
 */
public class AuthorizationService {

    /**
     * The method extracts the object Ids necessary for the authorization based on the class and method being invoked.
     *
     *
     * @return
     */
    String[] extractEntityIds(List<Object> parameters) {
        List<String> entityIds = new ArrayList<>();
        for (Object parameter : parameters) {
            if (parameter instanceof PipelineFamily) {
                PipelineFamily pipelineFamily = (PipelineFamily) parameter;
                entityIds.add(pipelineFamily.getPipelineDefinitionId());
                entityIds.add(pipelineFamily.getPipelineGroupId());
            } else if (parameter instanceof PipelineGroup) {
                Entity pipelineGroup = (Entity) parameter;
                entityIds.add(pipelineGroup.getId());
            }
        }

        return entityIds.toArray(new String[entityIds.size()]);

//        boolean hasPermission;
//        List<String> listOfIds = new ArrayList<>();
//
//        if (methodName.startsWith("getAll")) { // If no arguments are passed
////            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission);
//            return new String[0];
//        } else if (methodName.startsWith("getById") || methodName.startsWith("unassign")) { // If one String is passed as argument
////            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, arguments.get(0).toString());
//            listOfIds.add((String) arguments.get(0));
//        } else if (methodName.startsWith("assign")) { // If two Strings are passed as arguments
////            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, arguments.get(0).toString());
////            if (hasPermission) {
////                hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, arguments.get(1).toString());
////            }
//        } else {
//            switch (className) { // If an Object is passed as argument
//                case "PipelineGroupService":
//                    PipelineGroup pipelineGroup = (PipelineGroup) arguments.get(0);
//                    listOfIds.add(pipelineGroup.getId());
//                    break;
//                case "PipelineDefinitionService":
//                    PipelineDefinition pipelineDefinition = (PipelineDefinition) arguments.get(0);
//                    listOfIds.add(pipelineDefinition.getId());
//                    listOfIds.add(pipelineDefinition.getPipelineGroupId());
//                    break;
//                case "StageDefinitionService":
//                    StageDefinition stageDefinition = (StageDefinition) arguments.get(0);
//                    listOfIds.add(stageDefinition.getPipelineDefinitionId());
////                    entityIds.add(stageDefinition.getPipelineGroupId());
//                    break;
//                case "JobDefinitionService":
//                    JobDefinition jobDefinition = (JobDefinition) arguments.get(0);
//                    listOfIds.add(jobDefinition.getPipelineDefinitionId());
////                    entityIds.add(jobDefinition.getPipelineGroupId());
//                    break;
//                case "TaskDefinitionService":
//                    TaskDefinition taskDefinition = (TaskDefinition) arguments.get(0);
//                    listOfIds.add(taskDefinition.getPipelineDefinitionId());
////                    entityIds.add(taskDefinition.getPipelineGroupId());
//                    break;
//                case "PipelineService":
//                    Pipeline pipeline;
//                    if(methodName.startsWith("cancel") || methodName.startsWith("pause")){
//                        PipelineService pipelineService = new PipelineService();
//                        pipeline = (Pipeline) pipelineService.getById((String) arguments.get(0)).getObject();
//                    } else {
//                        pipeline = (Pipeline) arguments.get(0);
//                    }
//                    listOfIds.add(pipeline.getPipelineDefinitionId());
//                    break;
//            }
//
//
////            hasPermission = this.hasPermissionWithId(userPermissions, authorizedPermission, entityIds);
//        }
        //String[] entityIds = listOfIds.toArray(new String[listOfIds.size()]);
//        return null;
    }


    /**
     * The method tries to authorize against the User's Pipeline specific Permissions.
     * If none match the passed entityIds, tries to authorize against the User's PipelineGroup specific Permissions.
     * If none match the passed entityIds, tries to authorize against the User's generic Permissions.
     *
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
     *
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
     *
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
     *
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

    String[] getEntityIds(Object entity) {
        List<String> listOfIds = new ArrayList<>();
        String className = entity.getClass().getSimpleName();
        switch (className) { // If an Object is passed as argument
            case "PipelineGroupService":
                PipelineGroup pipelineGroup = (PipelineGroup) entity;
                listOfIds.add(pipelineGroup.getId());
                break;
            case "PipelineDefinitionService":
                PipelineDefinition pipelineDefinition = (PipelineDefinition) entity;
                listOfIds.add(pipelineDefinition.getId());
                listOfIds.add(pipelineDefinition.getPipelineGroupId());
                break;
            case "StageDefinitionService":
                StageDefinition stageDefinition = (StageDefinition) entity;
                listOfIds.add(stageDefinition.getPipelineDefinitionId());
//                    entityIds.add(stageDefinition.getPipelineGroupId());
                break;
            case "JobDefinitionService":
                JobDefinition jobDefinition = (JobDefinition) entity;
                listOfIds.add(jobDefinition.getPipelineDefinitionId());
//                    entityIds.add(jobDefinition.getPipelineGroupId());
                break;
            case "TaskDefinitionService":
                TaskDefinition taskDefinition = (TaskDefinition) entity;
                listOfIds.add(taskDefinition.getPipelineDefinitionId());
//                    entityIds.add(taskDefinition.getPipelineGroupId());
                break;
            case "PipelineService":
                Pipeline pipeline = (Pipeline) entity;
                listOfIds.add(pipeline.getPipelineDefinitionId());
                break;
        }

        String[] entityIds = listOfIds.toArray(new String[listOfIds.size()]);
        return entityIds;
    }

    /**
     * Determines the Permission Type of the user based on the entity IDs passed to it
     * @param userGrants
     * @param grantToEvaluateAgainst
     * @param entityIds
     * @return
     */
    public PermissionType determinePermissionTypeForUser(List<Grant> userGrants, Grant grantToEvaluateAgainst, String... entityIds) {
        PermissionType result = PermissionType.NONE;

        // Checks for specific Permissions, e.g. a user is assigned a permission for a specific entity (Pipeline/Group)
        for (String entityId : entityIds) {
            for (Grant grant : userGrants) {
                if (grant.getPermittedEntityId().equals(entityId)) {
                    if (grant.isGreaterThan(grantToEvaluateAgainst)) {
                        return grant.getType();
                    }
                }
            }
        }

        // Checks for generic Permissions, e.g. a user is assigned a permission for ALL Pipelines/Groups
        for (Grant grant : userGrants) {
            if (grant.getPermittedEntityId().startsWith("ALL") || grant.getScope().equals(PermissionScope.SERVER)) {
                if (grant.isGreaterThan(grantToEvaluateAgainst)) {
                    return grant.getType();
                }
            }
        }

        return result;
    }
}
