package net.hawkengine.model;

public class ExecTask extends Task {
    private String command;
    private String[] arguments;
    private String lookUpCommands;
    private String workingDirectory;
    private boolean ignoreErrors;

    public ExecTask() throws Exception {
        this.setType(TaskType.EXEC);
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String value) {
        this.command = value;
    }

    public String[] getArguments() {
        return this.arguments;
    }

    public void setArguments(String[] value) {
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

    public boolean getIgnoreErrors() {
        return this.ignoreErrors;
    }

    public void setIgnoreErrors(boolean value) {
        this.ignoreErrors = value;
    }
}
