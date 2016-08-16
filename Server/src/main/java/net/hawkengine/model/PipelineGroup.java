package net.hawkengine.model;

import java.util.ArrayList;
import java.util.List;

public class PipelineGroup extends DbEntry {
    private String name;
    private List<PipelineDefinition> pipelines;

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
}
