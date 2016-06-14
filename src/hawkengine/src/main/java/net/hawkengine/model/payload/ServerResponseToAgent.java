package net.hawkengine.model.payload;

import net.hawkengine.model.*;

import java.util.ArrayList;

public class ServerResponseToAgent {
    private Pipeline pipeline;
    private JobDefinition jobDefinition;
    private Stage stage;
    private EnvironmentInfo environmentInfo;
    private ArrayList<MaterialDefinition> materialDefinitions;
    private ArrayList<MaterialChange> materials;
    private ArrayList<EnvironmentVariable> environmentVariables;

    public ServerResponseToAgent() {
        this.setEnvironmentVariables(new ArrayList<>());
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public void setPipeline(Pipeline value) {
        this.pipeline = value;
    }

    public JobDefinition getJob() {
        return this.jobDefinition;
    }

    public void setJob(JobDefinition value) {
        this.jobDefinition = value;
    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage value) {
        this.stage = value;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return this.environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo value) {
        this.environmentInfo = value;
    }

    public ArrayList<MaterialDefinition> getMaterials() {
        return this.materialDefinitions;
    }

    public void setMaterials(ArrayList<MaterialDefinition> value) {
        this.materialDefinitions = value;
    }

    public ArrayList<MaterialChange> getExecutionMaterials() {
        return this.materials;
    }

    public void setExecutionMaterials(ArrayList<MaterialChange> value) {
        this.materials = value;
    }

    public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
        return this.environmentVariables;
    }

    public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
        this.environmentVariables = value;
    }

}
