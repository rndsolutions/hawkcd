package net.hawkengine.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class PipelineDefinitionRevision {
    private PipelineDefinition pipelineDefinition;
    private LocalDateTime revisionTime;
    private String userEmail;

    public PipelineDefinitionRevision(PipelineDefinition pipelineDefinition) {
        this.pipelineDefinition = pipelineDefinition;
        this.revisionTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
        this.userEmail = pipelineDefinition.getLastEditedBy();
    }

    public PipelineDefinition getPipelineDefinition() {
        return this.pipelineDefinition;
    }

    public LocalDateTime getRevisionTime() {
        return this.revisionTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
