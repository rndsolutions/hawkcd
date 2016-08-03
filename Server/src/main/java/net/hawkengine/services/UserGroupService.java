package net.hawkengine.services;

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
    private IUserService userService;

    public UserGroupService() {
        super.setRepository(new RedisRepository(UserGroup.class));
        this.userService = new UserService();
        super.setObjectType("UserGroup");
    }

    public UserGroupService(IDbRepository redisRepository, IUserService userService) {
        super.setRepository(redisRepository);
        this.userService = userService;
        super.setObjectType("UserGroup");
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
    public ServiceResult addUserToGroup(String userId, String groupId) {
        UserGroup userGroup = (UserGroup) this.getById(groupId).getObject();
        userGroup.getUserIds().add(userId);

        User user = (User) this.userService.getById(userId).getObject();
        user.getUserGroupIds().add(groupId);
        this.userService.update(user);

        return this.update(userGroup);
    }

    @Override
    public ServiceResult removeUserFromGroup(String userId, String groupId) {
        UserGroup userGroup = (UserGroup) this.getById(groupId).getObject();
        List<String> userIds = userGroup.getUserIds();

        for (Iterator<String> iter = userIds.listIterator(); iter.hasNext(); ) {
            String currentUserId = iter.next();
            if (currentUserId.equals(userId)) {
                iter.remove();
            }
        }

        userGroup.setUserIds(userIds);

        User user = (User) this.userService.getById(userId).getObject();
        List<String> userGroupIds = user.getUserGroupIds();

        for (Iterator<String> iter = userGroupIds.listIterator(); iter.hasNext(); ) {
            String currentUserGroupId = iter.next();
            if (currentUserGroupId.equals(groupId)) {
                iter.remove();
            }
        }

        user.setUserGroupIds(userGroupIds);

        ServiceResult updateUserServiceResult = this.userService.update(user);

        if (updateUserServiceResult.hasError()) {
            return updateUserServiceResult;
        }

        return this.update(userGroup);
    }

    @Override
    public ServiceResult getAllUserGroups() {
        List<UserGroup> userGroups = (List<UserGroup>) this.getAll().getObject();
        List<UserGroupDto> userGroupDtos = new ArrayList<>();

        for (UserGroup userGroup : userGroups) {
            List<String> userIds = userGroup.getUserIds();
            UserGroupDto userGroupDto = new UserGroupDto();
            userGroupDto.setId(userGroup.getId());
            userGroupDto.setUserGroupName(userGroup.getName());

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

}
