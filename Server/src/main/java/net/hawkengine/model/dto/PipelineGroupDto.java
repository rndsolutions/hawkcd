package net.hawkengine.model.dto;

import net.hawkengine.model.PermissionObject;

import java.util.List;

public class PipelineGroupDto extends PermissionObject {
    private String name;
    private List<PipelineDefinitionDto> pipelines;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PipelineDefinitionDto> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<PipelineDefinitionDto> pipelines) {
        this.pipelines = pipelines;
    }
}
