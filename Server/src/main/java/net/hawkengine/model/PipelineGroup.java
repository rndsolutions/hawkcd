package net.hawkengine.model;

import net.hawkengine.model.enums.PermissionType;

import java.util.ArrayList;
import java.util.List;

public class PipelineGroup extends DbEntry {
    private String name;
    private List<PipelineDefinition> pipelines;
    private PermissionType permissionType;

    public PipelineGroup(){
        this.setPipelines(new ArrayList<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public List<PipelineDefinition> getPipelines() {
        return this.pipelines;
    }

    public void setPipelines(List<PipelineDefinition> value) {
        this.pipelines = value;
    }

    public PermissionType getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
