
package net.hawkengine.agent.models;

import net.hawkengine.agent.enums.TaskType;

import java.util.List;

public class ExecTask extends TaskDefinition {
    private String command;
    private String arguments;
    private String lookUpCommands;
    private String workingDirectory;
    private boolean isIgnoringErrors;

    public ExecTask() {
        this.setType(TaskType.EXEC);
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String value) {
        this.command = value;
    }

    public String getArguments() {
        return this.arguments;
    }

    public void setArguments(String value) {
        this.arguments = value;
    }

    public String getLookUpCommands() {
        return this.lookUpCommands;
    }

    public void setLookUpCommands(String value) {
        this.lookUpCommands = value;
    }

    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    public void setWorkingDirectory(String value) {
        this.workingDirectory = value;
    }

    public boolean isIgnoringErrors() {
        return this.isIgnoringErrors;
    }

    public void setIgnoringErrors(boolean ignoringErrors) {
        this.isIgnoringErrors = ignoringErrors;
    }
}

