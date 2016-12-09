package io.hawkcd.services;

import io.hawkcd.core.security.Authorization;
import io.hawkcd.core.security.AuthorizationGrant;
import io.hawkcd.core.security.AuthorizationGrantService;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.UserGroup;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserWrapperService {
    private UserGroupService userGroupService;
    private UserService userService;
    private AuthorizationGrantService authorizationGrantService;

    public UserWrapperService() {
        this.userGroupService = new UserGroupService();
        this.userService = new UserService();
        this.authorizationGrantService = new AuthorizationGrantService();
    }

    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult updateUserPermissions(String userId, ArrayList<AuthorizationGrant> grants) {
        User user = (User) this.userService.getById(userId).getEntity();
        if (user == null) {
            return this.userService.createServiceResult(null, NotificationType.ERROR, "does not exist.");
        }

        List<AuthorizationGrant> updatedGrants;
        UserGroup userGroup = (UserGroup) this.userGroupService.getById(user.getUserGroupId()).getEntity();
        if (userGroup == null) {
            updatedGrants = this.authorizationGrantService.getUpdatedUserGrants(grants);
        } else {
            updatedGrants = this.authorizationGrantService.getUpdatedUserGrants(grants, userGroup.getPermissions());
        }

        user.setPermissions(updatedGrants);
        ServiceResult result = this.userService.update(user);

        return result;
    }

    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult updateUserGroupPermissions(String userGroupId, ArrayList<AuthorizationGrant> grants) {
        UserGroup userGroup = (UserGroup) this.userGroupService.getById(userGroupId).getEntity();
        if (userGroup == null) {
            return this.userGroupService.createServiceResult(null, NotificationType.ERROR, "does not exist.");
        }

        List<AuthorizationGrant> updatedGrants = this.authorizationGrantService.getUpdatedUserGroupGrants(grants);

        userGroup.setPermissions(updatedGrants);
        ServiceResult result = this.userGroupService.update(userGroup);
        if (result.getEntity() != null) {
            this.refreshUserGrants(userGroup.getUserIds());
        }

        return result;
    }

    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult assignUsers(String userGroupId, ArrayList<String> userIds) {
        UserGroup userGroupToUpdate = (UserGroup) this.userGroupService.getById(userGroupId).getEntity();
        if (userGroupToUpdate == null) {
            return this.userGroupService.createServiceResult(null, NotificationType.ERROR, "does not exist.");
        }

        List<String> usersToAssign = this.getUsersToAssign(userGroupToUpdate.getUserIds(), userIds);
        List<String> userToUnassigned = this.getUsersToUnassign(userGroupToUpdate.getUserIds(), userIds);

        userGroupToUpdate.setUserIds(userIds);
        ServiceResult result = this.userGroupService.update(userGroupToUpdate);

        UserGroup updatedUserGroup = (UserGroup) result.getEntity();
        if (updatedUserGroup != null) {
            for (String userId : usersToAssign) {
                this.assignUserToGroup(userId, updatedUserGroup);
            }

            for (String userId : userToUnassigned) {
                this.unassignUserFromGroup(userId);
            }
        }

        return result;
    }

    private void refreshUserGrants(Collection<String> userIds) {
        for (String userId : userIds) {
            User user = (User) this.userService.getById(userId).getEntity();
            if (user == null) {
                continue;
            }

            List<AuthorizationGrant> updatedGrants;
            UserGroup userGroup = (UserGroup) this.userGroupService.getById(user.getUserGroupId()).getEntity();
            if (userGroup == null) {
                updatedGrants = this.authorizationGrantService.getUpdatedUserGrants(user.getPermissions());
            } else {
                updatedGrants = this.authorizationGrantService.getUpdatedUserGrants(user.getPermissions(), userGroup.getPermissions());
            }

            user.setPermissions(updatedGrants);
            this.userService.update(user);
        }
    }

    private List<String> getUsersToAssign(List<String> oldUsers, List<String> newUsers) {
        List<String> usersToAssign = new ArrayList<>();
        for (String newUser : newUsers) {
            if (!oldUsers.contains(newUser)) {
                usersToAssign.add(newUser);
            }
        }

        return usersToAssign;
    }

    private List<String> getUsersToUnassign(List<String> oldUsers, List<String> newUsers) {
        List<String> usersToUnassign = new ArrayList<>();
        for (String oldUser : oldUsers) {
            if (!newUsers.contains(oldUser)) {
                usersToUnassign.add(oldUser);
            }
        }

        return usersToUnassign;
    }

    private void assignUserToGroup(String userId, UserGroup newUserGroup) {
        User user = (User) this.userService.getById(userId).getEntity();
        if (user == null) {
            return;
        }

        UserGroup oldUserGroup = (UserGroup) this.userGroupService.getById(user.getUserGroupId()).getEntity();
        if (oldUserGroup != null) {
            oldUserGroup.getUserIds().remove(userId);
            this.userGroupService.update(oldUserGroup);
        }

        List<AuthorizationGrant> updatedGrants = this.authorizationGrantService.getUpdatedUserGrants(user.getPermissions(), newUserGroup.getPermissions());

        user.setPermissions(updatedGrants);
        user.setUserGroupId(newUserGroup.getId());
        this.userService.update(user);
    }

    private void unassignUserFromGroup(String userId) {
        User user = (User) this.userService.getById(userId).getEntity();
        if (user == null) {
            return;
        }

        List<AuthorizationGrant> updatedGrants = this.authorizationGrantService.getUpdatedUserGrants(user.getPermissions());

        user.setPermissions(updatedGrants);
        user.setUserGroupId(null);
        this.userService.update(user);
    }
}
