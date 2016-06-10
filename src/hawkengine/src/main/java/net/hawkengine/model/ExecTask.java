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
        return command;
    }

    public void setCommand(String value) {
        command = value;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] value) {
        arguments = value;
    }

    public String getLookUpCommands() {
        return lookUpCommands;
    }

    public void setLookUpCommands(String value) {
        lookUpCommands = value;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String value) {
        workingDirectory = value;
    }

    public boolean getIgnoreErrors() {
        return ignoreErrors;
    }

    public void setIgnoreErrors(boolean value) {
        ignoreErrors = value;
    }
}
