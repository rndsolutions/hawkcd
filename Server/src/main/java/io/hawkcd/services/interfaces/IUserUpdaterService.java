package io.hawkcd.services.interfaces;

import io.hawkcd.core.security.AuthorizationGrant;
import io.hawkcd.model.ServiceResult;

import java.util.ArrayList;

public interface IUserUpdaterService {
    ServiceResult updateUserPermissions(String userId, ArrayList<AuthorizationGrant> grants);

    ServiceResult updateUserGroupPermissions(String userGroupId, ArrayList<AuthorizationGrant> grants);

    ServiceResult assignUsers(String userGroupId, ArrayList<String> userIds);
}
