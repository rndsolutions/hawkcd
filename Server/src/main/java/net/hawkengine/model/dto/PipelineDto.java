package net.hawkengine.model.dto;

import net.hawkengine.model.*;
import net.hawkengine.model.configuration.filetree.JsTreeFile;
import net.hawkengine.model.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PipelineDto extends PermissionObject {
    private String id;
    private String pipelineDefinitionId;
    private String pipelineDefinitionName;
    private int executionId;
    private List<Material> materials;
    private Status status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private String triggerReason;
    private List<JsTreeFile> artifactsFileStructure;
    private List<StageDto> stages;

    public PipelineDto() {
        this.stages = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void constructEmptyPipelineDto(PipelineDefinition pipelineDefinition) {
        int numberOfStageDefinitions = pipelineDefinition.getStageDefinitions().size();
        for (int i = 0; i < numberOfStageDefinitions; i++) {
            this.stages.add(new StageDto());
        }
    }

    public void constructBasePipelineDto(Pipeline pipeline) {
        this.setPermissionType(pipeline.getPermissionType());
        this.id = pipeline.getId();
        this.pipelineDefinitionId = pipeline.getPipelineDefinitionId();
        this.pipelineDefinitionName = pipeline.getPipelineDefinitionName();
        this.executionId = pipeline.getExecutionId();
        this.status = pipeline.getStatus();
        this.startTime = pipeline.getStartTime();
        this.endTime = pipeline.getEndTime();
        this.duration = pipeline.getDuration();
        this.triggerReason = pipeline.getTriggerReason();

        List<Stage> stages = pipeline.getStages();
        for (Stage pipelineStage : stages) {
            StageDto stageDto = new StageDto();
            stageDto.constructDto(pipelineStage);
            this.stages.add(stageDto);
        }
    }

    public void constructHistoryPipelineDto(Pipeline pipeline) {
        this.constructBasePipelineDto(pipeline);
        this.materials = pipeline.getMaterials();
    }

    public void constructArtifactPipelineDto(Pipeline pipeline) {
        this.constructHistoryPipelineDto(pipeline);
        this.artifactsFileStructure = pipeline.getArtifactsFileStructure();
    }
}
