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

package io.hawkcd.services.interfaces;

import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.UserGroup;
import io.hawkcd.model.dto.UserGroupDto;

public interface IUserGroupService extends ICrudService<UserGroup> {
    ServiceResult assignUserToGroup(User user, UserGroupDto userGroupDto);

    ServiceResult unassignUserFromGroup(User user, UserGroupDto userGroupDto);

    ServiceResult getAllUserGroups();

    ServiceResult addUserGroupDto(UserGroupDto userGroupDto);

    ServiceResult updateUserGroupDto(UserGroupDto userGroupDto);
}
