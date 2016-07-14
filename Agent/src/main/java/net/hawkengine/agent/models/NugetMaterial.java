package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.MaterialType;
import net.hawkengine.model.MaterialDefinition;

public class NugetMaterial extends MaterialDefinition {
    private String repositoryUrl;
    private String packageId;
    private boolean isPrerelease;
    private String packageVersion;

    public NugetMaterial() {
        super.setType(MaterialType.NUGET);
    }

    public String getRepositoryUrl() {
        return this.repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public boolean isPrerelease() {
        return this.isPrerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.isPrerelease = prerelease;
    }

    public String getPackageVersion() {
        return this.packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }
}
