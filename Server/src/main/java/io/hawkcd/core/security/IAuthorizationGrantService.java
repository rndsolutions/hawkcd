package io.hawkcd.core.security;

import java.util.List;

public interface IAuthorizationGrantService {
    List<AuthorizationGrant> getUpdatedUserGrants(List<AuthorizationGrant> userGrants);

    List<AuthorizationGrant> getUpdatedUserGrants(List<AuthorizationGrant> userGrants, List<AuthorizationGrant> userGroupGrants);

    List<AuthorizationGrant> getUpdatedUserGroupGrants(List<AuthorizationGrant> userGroupGrants);
}
