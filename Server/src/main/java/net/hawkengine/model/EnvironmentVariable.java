package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvironmentVariable extends DbEntry {
    private String key;
    private String value;
    private boolean isSecured;

    public EnvironmentVariable() {
    }

    public EnvironmentVariable(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String value) {
        this.key = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSecured() {
        return this.isSecured;
    }

    @JsonProperty("isSecured")
    public void setSecured(boolean value) {
        this.isSecured = value;
    }
}
