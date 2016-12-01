package io.hawkcd.core.security;

import java.util.List;

public interface IAuthorizationGrantService {
    List<AuthorizationGrant> sortAuthorizationGrants(List<AuthorizationGrant> grants);

    List<AuthorizationGrant> filterAuthorizationGrantDuplicates(List<AuthorizationGrant> currentGrants, List<AuthorizationGrant> updatedGrants);
}
