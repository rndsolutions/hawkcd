package net.hawkengine.services.interfaces;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;

public interface IUserGroupService extends ICrudService<UserGroup>{
    ServiceResult addUserToGroup(User user, String groupId);

    ServiceResult removeUserFromGroup(User user, String groupId);
}