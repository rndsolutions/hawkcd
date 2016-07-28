package net.hawkengine.services;

import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;
import net.hawkengine.services.interfaces.IUserGroupService;
import net.hawkengine.services.interfaces.IUserService;

import java.util.Iterator;
import java.util.List;

public class UserGroupService extends CrudService<UserGroup> implements IUserGroupService{
    private IUserService userService;

    public UserGroupService(){
        super.setRepository(new RedisRepository(UserGroup.class));
        this.userService = new UserService();
        super.setObjectType("UserGroup");
    }

    public UserGroupService(RedisRepository redisRepository, IUserService userService){
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
    public ServiceResult removeUserFromGroup(User user, String groupId) {
        UserGroup userGroup = (UserGroup) this.getById(groupId).getObject();
        List<String> userIds = userGroup.getUserIds();

        for (Iterator<String> iter = userIds.listIterator(); iter.hasNext(); ) {
            String userId = iter.next();
            if (userId.equals(user.getId())) {
                iter.remove();
            }
        }
        userGroup.setUserIds(userIds);

        return this.update(userGroup);
    }

}
