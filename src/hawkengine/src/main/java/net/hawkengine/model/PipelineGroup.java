package net.hawkengine.model;

import java.util.ArrayList;

public class PipelineGroup extends DbEntry {
    private String name;
    private ArrayList<Pipeline> pipelines;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public ArrayList<Pipeline> getPipelines() {
        return this.pipelines;
    }

    public void setPipelines(ArrayList<Pipeline> value) {
        this.pipelines = value;
    }
}
