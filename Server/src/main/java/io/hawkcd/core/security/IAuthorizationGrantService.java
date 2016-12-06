package io.hawkcd.core.security;

import java.util.Collection;
import java.util.List;

public interface IAuthorizationGrantService {
    List<AuthorizationGrant> getUpdatedGrants(String userGroupId, List<AuthorizationGrant> grants);

    List<AuthorizationGrant> getUpdatedGrants(String userId);

    void refreshUserGrants(Collection<String> userIds);

    List<AuthorizationGrant> sortAuthorizationGrants(List<AuthorizationGrant> grants);

    List<AuthorizationGrant> filterAuthorizationGrantsForDuplicates(List<AuthorizationGrant> grants);
}
