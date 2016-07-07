package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.hawkengine.model.enums.RunIf;
import net.hawkengine.model.enums.TaskType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExecTask.class, name = "EXEC"),
        @JsonSubTypes.Type(value = FetchArtifactTask.class, name = "FETCH_ARTIFACT"),
        @JsonSubTypes.Type(value = FetchMaterialTask.class, name = "FETCH_MATERIAL"),
        @JsonSubTypes.Type(value = UploadArtifactTask.class, name = "UPLOAD_ARTIFACT")})
public abstract class TaskDefinition extends DbEntry {
    private String name;
    private String jobDefinitionId;
    private String stageDefinitionId;
    private String pipelineDefinitionId;
    private TaskType type;
    private RunIf runIfCondition;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobDefinitionId() {
        return this.jobDefinitionId;
    }

    public void setJobDefinitionId(String jobDefinitionId) {
        this.jobDefinitionId = jobDefinitionId;
    }

    public String getStageDefinitionId() {
        return this.stageDefinitionId;
    }

    public void setStageDefinitionId(String stageDefinitionId) {
        this.stageDefinitionId = stageDefinitionId;
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public void setPipelineDefinitionId(String pipelineDefinitionId) {
        this.pipelineDefinitionId = pipelineDefinitionId;
    }

    public TaskType getType() {
        return this.type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public RunIf getRunIfCondition() {
        return this.runIfCondition;
    }

    public void setRunIfCondition(RunIf runIfCondition) {
        this.runIfCondition = runIfCondition;
    }
}
