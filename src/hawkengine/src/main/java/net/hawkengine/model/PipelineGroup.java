package net.hawkengine.model;

import java.util.ArrayList;

public class PipelineGroup extends DbEntry {
    private String name;
    private ArrayList<Pipeline> pipelines;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public ArrayList<Pipeline> getPipelines() {
        return pipelines;
    }

    public void setPipelines(ArrayList<Pipeline> value) {
        pipelines = value;
    }

}
