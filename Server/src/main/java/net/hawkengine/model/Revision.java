package net.hawkengine.model;

import java.util.ArrayList;
import java.util.List;

public class Revision extends DbEntry {
    private String pipelineDefinitionId;
    private String pipelineDefinitionName;
    private List<PipelineDefinitionRevision> revisions;

    public Revision(String pipelineDefinitionId, String pipelineDefinitionName) {
        this.pipelineDefinitionId = pipelineDefinitionId;
        this.pipelineDefinitionName = pipelineDefinitionName;
        this.revisions = new ArrayList<>();
    }

    public String getPipelineDefinitionId() {
        return this.pipelineDefinitionId;
    }

    public String getPipelineDefinitionName() {
        return this.pipelineDefinitionName;
    }

    public List<PipelineDefinitionRevision> getRevisions() {
        return this.revisions;
    }
}
