package io.hawkcd.core.security;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationGrantService implements IAuthorizationGrantService{

    @Override
    public List<AuthorizationGrant> sortAuthorizationGrants(List<AuthorizationGrant> grants) {
        List<AuthorizationGrant> sortedGrants = grants
                .stream()
                .sorted(Comparator.comparingInt(g -> g.getPermissionEntity().getPriorityLevel()))
                .collect(Collectors.toList());

        return sortedGrants;
    }

    // Does not work properly
    @Override
    public List<AuthorizationGrant> filterAuthorizationGrantDuplicates(List<AuthorizationGrant> currentGrants, List<AuthorizationGrant> updatedGrants) {
        List<AuthorizationGrant> filteredGrants = new ArrayList<>();
        for (AuthorizationGrant currentGrant : currentGrants) {
            for (AuthorizationGrant updatedGrant : updatedGrants) {
                boolean isDuplicate = currentGrant.isDuplicateWith(updatedGrant);
                if (!isDuplicate) {
                    filteredGrants.add(updatedGrant);
                }
            }
        }

        return filteredGrants;
    }
}
