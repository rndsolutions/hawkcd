package io.hawkcd.core.security;

import io.hawkcd.model.enums.PermissionEntity;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationGrantServiceTest {
    private IAuthorizationGrantService authorizationGrantService;

    @Before
    public void setUp() throws Exception {
        this.authorizationGrantService = new AuthorizationGrantService();
    }

    @Test
    public void sortAuthorizationGrants() {
        // Arrange
        List<AuthorizationGrant> grants = new ArrayList<>();
        AuthorizationGrant serverEntityGrant = new AuthorizationGrant(PermissionScope.SERVER, PermissionType.NONE, PermissionEntity.SERVER);
        AuthorizationGrant allPipelineGroupsEntityGrant = new AuthorizationGrant(PermissionScope.SERVER, PermissionType.NONE, PermissionEntity.ALL_PIPELINE_GROUPS);
        AuthorizationGrant allPipelinesEntityGrant = new AuthorizationGrant(PermissionScope.SERVER, PermissionType.NONE, PermissionEntity.ALL_PIPELINES);
        grants.add(serverEntityGrant);
        grants.add(allPipelineGroupsEntityGrant);
        grants.add(allPipelinesEntityGrant);

        // Act
        List<AuthorizationGrant> sortedGrants = this.authorizationGrantService.sortAuthorizationGrants(grants);

        // Assert
        Assert.assertTrue(sortedGrants.get(0).getPermissionEntity() == PermissionEntity.ALL_PIPELINES);
        Assert.assertTrue(sortedGrants.get(1).getPermissionEntity() == PermissionEntity.ALL_PIPELINE_GROUPS);
        Assert.assertTrue(sortedGrants.get(2).getPermissionEntity() == PermissionEntity.SERVER);
    }

    @Test
    public void filterAuthorizationGrantsForDuplicates() {

    }

}