package net.hawkengine.services;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;
import net.hawkengine.model.dto.UserGroupDto;
import net.hawkengine.services.interfaces.IUserGroupService;
import net.hawkengine.services.interfaces.IUserService;

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
    public ServiceResult update(UserGroup userGroup) {
        return super.update(userGroup);
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
            if (removeGroupFromAllUsers.hasError()) {
                return removeGroupFromAllUsers;
            }
        }

        return super.delete(id);
    }

    @Override
    public ServiceResult assignUserToGroup(User user, UserGroup userGroup) {
        userGroup = (UserGroup) this.getById(userGroup.getId()).getObject();

        boolean userHasGroupId = user.getUserGroupIds().contains(userGroup.getId());
        boolean groupHasUserId = userGroup.getUserIds().contains(user.getId());

        ServiceResult userGroupResult = new ServiceResult();
        if (userHasGroupId && !groupHasUserId) {
            userGroup.getUserIds().add(user.getId());
            this.userService.update(user);
            this.update(userGroup);
            UserGroupDto userGroupDto = this.getUserGroupDto(userGroup);

            ServiceResult userResult = new ServiceResult();
            userResult.setError(false);
            userResult.setMessage("User assigned successfully.");
            userResult.setObject(user);
            EndpointConnector.passResultToEndpoint("UserService", "update", userResult);

            userGroupResult.setError(false);
            userGroupResult.setMessage("UserGroup updated successfully.");
            userGroupResult.setObject(userGroupDto);
        } else {
            userGroupResult.setError(true);
            userGroupResult.setMessage("User already assigned to User Group.");
            userGroupResult.setObject(null);
        }

        return userGroupResult;
    }

    @Override
    public ServiceResult unassignUserFromGroup(User user, UserGroup userGroup) {
        userGroup = (UserGroup) this.getById(userGroup.getId()).getObject();

        boolean userHasGroupId = user.getUserGroupIds().contains(userGroup.getId());
        boolean groupHasUserId = userGroup.getUserIds().contains(user.getId());

        ServiceResult userGroupResult = new ServiceResult();
        if (!userHasGroupId && groupHasUserId) {
            userGroup.getUserIds().remove(user.getId());
            this.userService.update(user);
            this.update(userGroup);
            UserGroupDto userGroupDto = this.getUserGroupDto(userGroup);

            ServiceResult userResult = new ServiceResult();
            userResult.setError(false);
            userResult.setMessage("User unassigned successfully.");
            userResult.setObject(user);
            EndpointConnector.passResultToEndpoint("UserService", "update", userResult);

            userGroupResult.setError(false);
            userGroupResult.setMessage("UserGroup updated successfully.");
            userGroupResult.setObject(userGroupDto);
        } else {
            userGroupResult.setError(true);
            userGroupResult.setMessage("User already unassigned from User Group.");
            userGroupResult.setObject(null);
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

            for (String userId : userIds) {
                User currentUser = (User) this.userService.getById(userId).getObject();
                userGroupDto.getUsers().add(currentUser);
            }

            userGroupDtos.add(userGroupDto);
        }
        ServiceResult userGroupDtosServiceResult = new ServiceResult();
        userGroupDtosServiceResult.setError(false);
        userGroupDtosServiceResult.setMessage("User Groups retrieved successfully.");
        userGroupDtosServiceResult.setObject(userGroupDtos);

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
