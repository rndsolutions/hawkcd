package net.hawkengine.model.dto;

import net.hawkengine.model.PermissionObject;
import net.hawkengine.model.PipelineGroup;

import java.util.List;

public class PipelineGroupDto extends PermissionObject {
    private String id;
    private String name;
    private List<PipelineDefinitionDto> pipelines;

    public List<PipelineDefinitionDto> getPipelines() {
        return pipelines;
    }

    public void setPipelines(List<PipelineDefinitionDto> pipelines) {
        this.pipelines = pipelines;
    }

    public void constructDto(PipelineGroup pipelineGroup) {
        this.setPermissionType(pipelineGroup.getPermissionType());
        this.id = pipelineGroup.getId();
        this.name = pipelineGroup.getName();
    }
}
