package net.hawkengine.model.dto;

import net.hawkengine.model.PermissionObject;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;

public class PipelineDefinitionDto extends PermissionObject{
    private String id;
    private String name;
    private PipelineDto lastRun;

    public PipelineDefinitionDto() {
        this.lastRun = new PipelineDto();
    }

    public void constructDto(PipelineDefinition pipelineDefinition, Pipeline pipeline) {
        this.id = pipelineDefinition.getId();
        this.name = pipelineDefinition.getName();
        if (pipeline != null) {
            this.lastRun.constructBasePipelineDto(pipeline);
        } else {
            this.lastRun.constructEmptyPipelineDto(pipelineDefinition);
        }
    }
}
