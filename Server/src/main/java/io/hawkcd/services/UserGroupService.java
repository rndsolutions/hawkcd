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

package io.hawkcd.services;

import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.UserGroup;
import io.hawkcd.model.dto.UserGroupDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.interfaces.IUserGroupService;
import io.hawkcd.services.interfaces.IUserService;
import io.hawkcd.ws.EndpointConnector;
import io.hawkcd.ws.SessionPool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserGroupService extends CrudService<UserGroup> implements IUserGroupService {
    private static final Class CLASS_TYPE = UserGroup.class;

    private IUserService userService;

    public UserGroupService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        this.userService = new UserService();
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public UserGroupService(IDbRepository repository, IUserService userService) {
        super.setRepository(repository);
        this.userService = userService;
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getById(String id) {
        return super.getById(id);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(UserGroup userGroup) {
        return super.add(userGroup);
    }

    @Override
    public ServiceResult addUserGroupDto(UserGroupDto userGroupDto) {
        UserGroup userGroup = new UserGroup();
        userGroup.setName(userGroupDto.getName());

        this.add(userGroup);

        UserGroupDto updatedUserGroupDto = this.getUserGroupDto(userGroup);
        ServiceResult result = new ServiceResult(updatedUserGroupDto, NotificationType.SUCCESS, "UserGroup created successfully.");

        return result;
    }

    @Override
    public ServiceResult update(UserGroup userGroup) {
        return super.update(userGroup);
    }

    @Override
    public ServiceResult updateUserGroupDto(UserGroupDto userGroupDto) {
        UserGroup userGroup = (UserGroup) this.getById(userGroupDto.getId()).getObject();
        userGroup.setName(userGroupDto.getName());
        userGroup.setUserIds(userGroupDto.getUserIds());
        userGroup.setPermissions(userGroupDto.getPermissions());

        this.update(userGroup);

        UserGroupDto updatedUserGroupDto = this.getUserGroupDto(userGroup);
        ServiceResult result = new ServiceResult(updatedUserGroupDto, NotificationType.SUCCESS, "UserGroup updated successfully.");

        for (String userId : userGroup.getUserIds()) {
            SessionPool.getInstance().updateUserObjects(userId);
        }

        return result;
    }

    @Override
    public ServiceResult delete(String id) {
        List<User> users = (List<User>) this.userService.getAll().getObject();

        for (User user : users) {
            List<String> userGroupIds = user.getUserGroupIds();

            for (Iterator<String> iter = userGroupIds.listIterator(); iter.hasNext(); ) {
                String currentUserGroupId = iter.next();
                if (currentUserGroupId.equals(id)) {
                    iter.remove();
                }
            }

            user.setUserGroupIds(userGroupIds);
            ServiceResult removeGroupFromAllUsers = this.userService.update(user);
            if (removeGroupFromAllUsers.getNotificationType() == NotificationType.ERROR) {
                return removeGroupFromAllUsers;
            }
        }

        return super.delete(id);
    }

    @Override
    public ServiceResult assignUserToGroup(User user, UserGroupDto userGroupDto) {
        UserGroup userGroup = (UserGroup) this.getById(userGroupDto.getId()).getObject();

        boolean userHasGroupId = user.getUserGroupIds().contains(userGroup.getId());
        boolean groupHasUserId = userGroup.getUserIds().contains(user.getId());

        ServiceResult userGroupResult;
        if (userHasGroupId && !groupHasUserId) {
            userGroup.getUserIds().add(user.getId());
            this.userService.update(user);
            this.update(userGroup);
            UserGroupDto userGroupDtoResult = this.getUserGroupDto(userGroup);

            ServiceResult userResult = new ServiceResult(user, NotificationType.SUCCESS, "User assigned successfully.");
            EndpointConnector.passResultToEndpoint("UserService", "update", userResult);
            SessionPool.getInstance().updateUserObjects(user.getId());

            userGroupResult = new ServiceResult(userGroupDtoResult, NotificationType.SUCCESS, "UserGroup updated successfully.");
        } else {
            userGroupResult = new ServiceResult(null, NotificationType.ERROR, "User already assigned to User Group.");
        }

        return userGroupResult;
    }

    @Override
    public ServiceResult unassignUserFromGroup(User user, UserGroupDto userGroupDto) {
        UserGroup userGroup = (UserGroup) this.getById(userGroupDto.getId()).getObject();

        boolean userHasGroupId = user.getUserGroupIds().contains(userGroup.getId());
        boolean groupHasUserId = userGroup.getUserIds().contains(user.getId());

        ServiceResult userGroupResult = null;
        if (!userHasGroupId && groupHasUserId) {
            userGroup.getUserIds().remove(user.getId());
            this.userService.update(user);
            this.update(userGroup);
            UserGroupDto userGroupDtoResult = this.getUserGroupDto(userGroup);

            ServiceResult userResult = new ServiceResult(user, NotificationType.SUCCESS, "User unassigned successfully.");
            EndpointConnector.passResultToEndpoint("UserService", "update", userResult);
            SessionPool.getInstance().updateUserObjects(user.getId());

            userGroupResult = new ServiceResult(userGroupDtoResult, NotificationType.SUCCESS, "UserGroup updated successfully.");
        } else {
            userGroupResult = new ServiceResult(null, NotificationType.ERROR, "User already unassigned from User Group.");
        }

        return userGroupResult;
    }

    @Override
    public ServiceResult getAllUserGroups() {
        List<UserGroup> userGroups = (List<UserGroup>) this.getAll().getObject();
        List<UserGroupDto> userGroupDtos = new ArrayList<>();

        for (UserGroup userGroup : userGroups) {
            List<String> userIds = userGroup.getUserIds();
            UserGroupDto userGroupDto = new UserGroupDto();
            userGroupDto.setId(userGroup.getId());
            userGroupDto.setName(userGroup.getName());
            userGroupDto.setPermissions(userGroup.getPermissions());
            userGroupDto.setUserIds(userGroup.getUserIds());

            for (String userId : userIds) {
                User currentUser = (User) this.userService.getById(userId).getObject();
                userGroupDto.getUsers().add(currentUser);
            }

            userGroupDtos.add(userGroupDto);
        }
        ServiceResult userGroupDtosServiceResult = new ServiceResult(userGroupDtos, NotificationType.SUCCESS, "User Groups retrieved successfully.");

        return userGroupDtosServiceResult;
    }

    private UserGroupDto getUserGroupDto(UserGroup userGroup) {
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setId(userGroup.getId());
        userGroupDto.setName(userGroup.getName());
        userGroupDto.setUserIds(userGroup.getUserIds());
        userGroupDto.setPermissions(userGroup.getPermissions());
        userGroupDto.setUsers(new ArrayList<>());
        List<String> userIds = userGroupDto.getUserIds();
        for (String userId : userIds) {
            User user = (User) this.userService.getById(userId).getObject();
            userGroupDto.getUsers().add(user);
        }

        return userGroupDto;
    }
}
