package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvironmentVariable {
    private String name;
    private String value;
    private boolean isSecured;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
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
