package io.hawkcd.core.security;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationGrantService implements IAuthorizationGrantService {
    @Override
    public List<AuthorizationGrant> getUpdatedUserGrants(List<AuthorizationGrant> userGrants) {
        return this.getUpdatedUserGrants(userGrants, new ArrayList<>());
    }

    @Override
    public List<AuthorizationGrant> getUpdatedUserGrants(List<AuthorizationGrant> userGrants, List<AuthorizationGrant> userGroupGrants) {
        List<AuthorizationGrant> updatedGrants = new ArrayList<>();

        updatedGrants.addAll(userGrants);
        updatedGrants = this.removeInheritedGrants(updatedGrants);

        updatedGrants.addAll(userGroupGrants);
        updatedGrants = this.removeDuplicateGrants(updatedGrants);
        updatedGrants = this.sortGrants(updatedGrants);

        return updatedGrants;
    }

    @Override
    public List<AuthorizationGrant> getUpdatedUserGroupGrants(List<AuthorizationGrant> userGroupGrants) {
        List<AuthorizationGrant> updatedGrants = new ArrayList<>();

        updatedGrants.addAll(userGroupGrants);
        for (AuthorizationGrant updatedGrant : updatedGrants) {
            updatedGrant.setInherited(true);
        }

        updatedGrants = this.removeDuplicateGrants(updatedGrants);
        updatedGrants = this.sortGrants(updatedGrants);

        return updatedGrants;
    }

    private List<AuthorizationGrant> removeInheritedGrants(List<AuthorizationGrant> grants) {
        List<AuthorizationGrant> filteredGrants = grants
                .stream()
                .filter(g -> !g.isInherited())
                .collect(Collectors.toList());

        return filteredGrants;
    }

    private List<AuthorizationGrant> removeDuplicateGrants(List<AuthorizationGrant> grants) {
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

    private List<AuthorizationGrant> sortGrants(List<AuthorizationGrant> grants) {
        List<AuthorizationGrant> sortedGrants = grants
                .stream()
                .sorted(Comparator.comparingInt(g -> g.getPermissionEntity().getPriorityLevel()))
                .collect(Collectors.toList());
        Collections.reverse(sortedGrants);

        return sortedGrants;
    }
}
