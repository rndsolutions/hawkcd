package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;

import java.util.List;

public interface IUserGroupService extends ICrudService<UserGroup>{
    ServiceResult assignUsersToGroup(List<User> users, UserGroup userGroup);

    ServiceResult unassignUsersFromGroup(List<User> users, UserGroup userGroup);

    ServiceResult getAllUserGroups();
}
