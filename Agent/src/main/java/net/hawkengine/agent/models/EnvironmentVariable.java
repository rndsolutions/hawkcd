package net.hawkengine.agent.models;

/// <summary>
/// If the environment variable is isSecured, its value is encrypted. If not, it is saved in plain text.
/// Also additional attribute "secure=true" is added in the XML.
/// </summary>
public class EnvironmentVariable {
    private String name;
    private String value;
    private Boolean isSecured;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isSecured() {
        return isSecured;
    }

    public void setSecured(Boolean isSecured) {
        this.isSecured = isSecured;
    }
}

