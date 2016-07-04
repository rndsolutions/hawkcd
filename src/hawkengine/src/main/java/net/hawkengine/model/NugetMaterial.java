package net.hawkengine.model;

import net.hawkengine.model.enums.MaterialType;

public class NugetMaterial extends MaterialDefinition{
    private String url;
    private String packageId;
    private boolean isPrereleaseIncluded;
    private String latestPackegeVersion;

    public NugetMaterial() {
        this.setType(MaterialType.NUGET);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public boolean isPrereleaseIncluded() {
        return this.isPrereleaseIncluded;
    }

    public void setPrereleaseIncluded(boolean prereleaseIncluded) {
        this.isPrereleaseIncluded = prereleaseIncluded;
    }

    public String getLatestPackegeVersion() {
        return this.latestPackegeVersion;
    }

    public void setLatestPackegeVersion(String latestPackegeVersion) {
        this.latestPackegeVersion = latestPackegeVersion;
    }
}
