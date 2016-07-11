package net.hawkengine.agent.models.payload;

import net.hawkengine.agent.enums.ArtifactInfoType;

import java.util.List;

public class ArtifactInfo {
    private String name;
    private String url;
    private ArtifactInfoType type;
    private List<ArtifactInfo> files;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArtifactInfoType getType() {
        return type;
    }

    public void setType(ArtifactInfoType type) {
        this.type = type;
    }

    public List<ArtifactInfo> getFiles() {
        return files;
    }

    public void setFiles(List<ArtifactInfo> files) {
        this.files = files;
    }
}
