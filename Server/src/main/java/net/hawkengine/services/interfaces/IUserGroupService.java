package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;
import net.hawkengine.model.dto.UserGroupDto;

public interface IUserGroupService extends ICrudService<UserGroup>{
    ServiceResult assignUserToGroup(User user, UserGroupDto userGroupDto);

    ServiceResult unassignUserFromGroup(User user, UserGroupDto userGroupDto);

    ServiceResult getAllUserGroups();

    ServiceResult addUserGroupDto(UserGroupDto userGroupDto);

    ServiceResult updateUserGroupDto(UserGroupDto userGroupDto);
}
