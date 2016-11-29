/* *   Copyright (C) 2016 R&D Solutions Ltd.
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

        import io.hawkcd.core.Message;
import io.hawkcd.model.Entity;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.PipelineGroupDto;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.PermissionType;

import java.util.List;

public interface IAuthorizationManager {

    /*
    * Returns true, false if the user being evaluated has rights to perform an operation
    * @param user
    */
    boolean isAuthorized(User user, WsContractDto contract, List<Object> parameters) throws ClassNotFoundException, NoSuchMethodException;

    PermissionType determinePermissionTypeForEntity(List<AuthorizationGrant> userGrants, Object object, List<Object> parameters);

    PermissionType determinePermissionTypeForEntity(List<AuthorizationGrant> userGrants, Object object);

    PermissionType determinePermissionTypeForUser(List<AuthorizationGrant> userGrants, AuthorizationGrant grantToEvaluateAgainst, String... entityIds);

    String[] extractEntityIds(List<Object> parameters);

    Message constructAuthorizedMessage(ServiceResult result, String className, String methodName);

    Message attachPermissionTypeMapToMessage(Message message, List<Object> methodArgs);

    List<Entity> attachPermissionTypeToList(Message message, List<Object> parameters);

    List<PipelineGroupDto> attachPermissionsToPipelineDtos(List<PipelineGroupDto> pipelineGroupDtos, User currentUser);
}