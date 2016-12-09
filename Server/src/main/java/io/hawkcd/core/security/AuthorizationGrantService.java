package io.hawkcd.core.security;

import com.google.common.collect.Lists;
import io.hawkcd.model.User;
import io.hawkcd.model.UserGroup;
import io.hawkcd.services.UserGroupService;
import io.hawkcd.services.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationGrantService {
    private static UserGroupService userGroupService = new UserGroupService();
    private static UserService userService = new UserService();

    public static List<AuthorizationGrant> getUpdatedGrants(String userGroupId, List<AuthorizationGrant> grants) {
        List<AuthorizationGrant> updatedGrants = new ArrayList<>();
        for (AuthorizationGrant grant : grants) {
            if (!grant.isInherited()) {
                updatedGrants.add(grant);
            }
        }

//        updatedGrants.addAll(grants);

        UserGroup userGroup = null;
        if (userGroupId != null) {
            userGroup = (UserGroup) userGroupService.getById(userGroupId).getEntity();
        }

        if (userGroup != null) {
            updatedGrants.addAll(userGroup.getPermissions());
        }

        updatedGrants = filterAuthorizationGrantsForDuplicates(updatedGrants);
        updatedGrants = sortAuthorizationGrants(updatedGrants);

        return updatedGrants;
    }

//    public static List<AuthorizationGrant> getUpdatedGrants(String userId) {
//        User user = (User) userService.getById(userId).getEntity();
//        if (user == null) {
//            return null;
//        }
//
//        List<AuthorizationGrant> updatedGrants = new ArrayList<>();
//        updatedGrants.addAll(user.getPermissions());
//
//        UserGroup userGroup = (UserGroup) userGroupService.getById(user.getUserGroupId()).getEntity();
//        if (userGroup != null) {
//            updatedGrants.addAll(userGroup.getPermissions());
//        }
//
//        updatedGrants = filterAuthorizationGrantsForDuplicates(updatedGrants);
//        updatedGrants = sortAuthorizationGrants(updatedGrants);
//
//        return updatedGrants;
//    }

    public static void refreshUserGrants(Collection<String> userIds) {
        for (String userId : userIds) {
            User user = (User) userService.getById(userId).getEntity();
            if (user == null) {
                continue;
            }

            List<AuthorizationGrant> newGrants = new ArrayList<>();
            List<AuthorizationGrant> userGrants = user.getPermissions();
            for (AuthorizationGrant grant : userGrants) {
                if (!grant.isInherited()) {
                    newGrants.add(grant);
                }
            }

            if (user.getUserGroupId() != null) {
                UserGroup userGroup = (UserGroup) userGroupService.getById(user.getUserGroupId()).getEntity();
                if (userGroup != null) {
                    newGrants.addAll(userGroup.getPermissions());
                }
            }

            newGrants = filterAuthorizationGrantsForDuplicates(newGrants);
            newGrants = sortAuthorizationGrants(newGrants);
            user.setPermissions(newGrants);
            userService.update(user);
        }
    }

    public static List<AuthorizationGrant> sortAuthorizationGrants(List<AuthorizationGrant> grants) {
        List<AuthorizationGrant> sortedGrants = grants
                .stream()
                .sorted(Comparator.comparingInt(g -> g.getPermissionEntity().getPriorityLevel()))
                .collect(Collectors.toList());
        sortedGrants = Lists.reverse(sortedGrants);

        return sortedGrants;
    }

    public static List<AuthorizationGrant> filterAuthorizationGrantsForDuplicates(List<AuthorizationGrant> grants) {
        List<AuthorizationGrant> filteredGrants = new ArrayList<>();
        for (AuthorizationGrant grant : grants) {
            boolean foundDuplicate = false;
            for (AuthorizationGrant filteredGrant : filteredGrants) {
                foundDuplicate = grant.isDuplicateWith(filteredGrant);
                if (foundDuplicate) {
                    break;
                }
            }

            if (!foundDuplicate) {
                filteredGrants.add(grant);
            }
        }

        return filteredGrants;
    }
}
