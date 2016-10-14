package net.hawkengine.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class PipelineDefinitionRevision {
    private PipelineDefinition pipelineDefinition;
    private LocalDateTime revisionTime;

    public PipelineDefinitionRevision(PipelineDefinition pipelineDefinition) {
        this.pipelineDefinition = pipelineDefinition;
        this.revisionTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
    }

    public PipelineDefinition getPipelineDefinition() {
        return this.pipelineDefinition;
    }

    public LocalDateTime getRevisionTime() {
        return this.revisionTime;
    }
}
