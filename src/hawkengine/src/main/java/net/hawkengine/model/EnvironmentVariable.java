package net.hawkengine.model;

public class EnvironmentVariable {
    private String name;
    private String value;
    private boolean isSecured;

    public EnvironmentVariable(String name, String value, boolean isSecured) {
        this(name, value);
        this.setSecured(isSecured);
    }

    public EnvironmentVariable(String name, String value) {
        this.setName(name);
        this.setValue(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public void setSecured(boolean value) {
        isSecured = value;
    }
}
