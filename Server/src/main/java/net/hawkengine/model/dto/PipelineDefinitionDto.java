package net.hawkengine.model.dto;

import net.hawkengine.model.PermissionObject;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;

import java.util.ArrayList;
import java.util.List;

public class PipelineDefinitionDto extends PermissionObject {
    private String id;
    private String name;
    private PipelineDto lastRun;
    private List<Integer> pipelineExecutionIds;

    public PipelineDefinitionDto() {
        this.lastRun = new PipelineDto();
        this.pipelineExecutionIds = new ArrayList<>();
    }

    public void constructDto(PipelineDefinition pipelineDefinition, List<Pipeline> pipelines) {
        this.id = pipelineDefinition.getId();
        this.name = pipelineDefinition.getName();
        if (!pipelines.isEmpty()) {
            int numberOfPipelines = pipelines.size();
            for (int i = 1; i <= numberOfPipelines; i++) {
                this.pipelineExecutionIds.add(pipelines.get(i).getExecutionId());
                if (i == numberOfPipelines) {
                    this.lastRun.constructBasePipelineDto(pipelines.get(i));
                }
            }
        } else {
            this.lastRun.constructEmptyPipelineDto(pipelineDefinition);
        }
    }
}
