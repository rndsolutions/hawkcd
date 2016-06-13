package net.hawkengine.model;

import java.util.List;

public class StageDefinition extends DbEntry{
    private String name;
    private String pipelineDefinitionId;
    private List<EnvironmentVariable> environmentVariables;
    private List<JobDefinition> jobs;
    private boolean isAutoSchedulingEnabled;
    private boolean isLocked;
}
