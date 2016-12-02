package io.hawkcd.core.security;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationGrantService implements IAuthorizationGrantService {

    @Override
    public List<AuthorizationGrant> sortAuthorizationGrants(List<AuthorizationGrant> grants) {
        List<AuthorizationGrant> sortedGrants = grants
                .stream()
                .sorted(Comparator.comparingInt(g -> g.getPermissionEntity().getPriorityLevel()))
                .collect(Collectors.toList());
        sortedGrants = Lists.reverse(sortedGrants);

        return sortedGrants;
    }

    @Override
    public List<AuthorizationGrant> filterAuthorizationGrantsForDuplicates(List<AuthorizationGrant> grants) {
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
