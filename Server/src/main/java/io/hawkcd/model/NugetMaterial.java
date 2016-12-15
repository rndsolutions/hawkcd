package io.hawkcd.model;

import io.hawkcd.core.security.Authorization;
import io.hawkcd.model.enums.MaterialType;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;

@Authorization(scope = PermissionScope.SERVER, type = PermissionType.VIEWER)
public class NugetMaterial extends MaterialDefinition {
    private String repositoryUrl;
    private String packageId;
    private String packageVersion;
    private String isPrerelease;
    private String username;
    private String password;

    public NugetMaterial() {
        super.setType(MaterialType.NUGET);
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getIsPrerelease() {
        return isPrerelease;
    }

    public void setIsPrerelease(String isPrerelease) {
        this.isPrerelease = isPrerelease;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
