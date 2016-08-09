package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.UserGroup;
import net.hawkengine.model.dto.UserGroupDto;

public interface IUserGroupService extends ICrudService<UserGroup>{
    ServiceResult assignUserToGroup(User user, UserGroup userGroup);

    ServiceResult unassignUserFromGroup(User user, UserGroup userGroup);

    ServiceResult getAllUserGroups();

    ServiceResult updateUserGroupDto(UserGroupDto userGroupDto);
}
