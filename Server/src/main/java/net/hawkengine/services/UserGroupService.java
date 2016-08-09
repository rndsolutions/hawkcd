package net.hawkengine.services;

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
        userGroup.getUserIds().add(user.getId());

        String userGroupId = userGroup.getId();
        List<String> userGroupIds = user.getUserGroupIds();

        String exists = userGroupIds.stream().filter(u -> u.equals(userGroupId)).findAny().orElse(null);

        if (exists != null) {
            ServiceResult result = new ServiceResult();
            result.setError(true);
            result.setMessage("User is already in this User Group.");
            result.setObject(null);

            return result;
        }

        user.getUserGroupIds().add(userGroup.getId());
        ServiceResult updateUserServiceResult = this.userService.update(user);

        if (updateUserServiceResult.hasError()) {

            return updateUserServiceResult;
        }

        return this.update(userGroup);
    }

    @Override
    public ServiceResult unassignUserFromGroup(User user, UserGroup userGroup) {
        List<String> userIds = userGroup.getUserIds();

        for (Iterator<String> iter = userIds.listIterator(); iter.hasNext(); ) {
            String currentUserId = iter.next();
            if (currentUserId.equals(user.getId())) {
                iter.remove();
            }
        }
        List<String> userGroupIds = user.getUserGroupIds();

        for (Iterator<String> iter = userGroupIds.listIterator(); iter.hasNext(); ) {
            String currentUserGroupId = iter.next();
            if (currentUserGroupId.equals(userGroup.getId())) {
                iter.remove();
            }
        }

        user.setUserGroupIds(userGroupIds);

        ServiceResult updateUserServiceResult = this.userService.update(user);

        if (updateUserServiceResult.hasError()) {
            return updateUserServiceResult;
        }


        userGroup.setUserIds(userIds);

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

}
