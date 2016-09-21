package net.hawkengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvironmentVariable extends DbEntry {
    private String key;
    private String value;
    private boolean isSecured;
    private boolean isDeletable;

    public EnvironmentVariable() {
        this.setDeletable(true);
    }

    public EnvironmentVariable(String key, String value) {
        this.key = key;
        this.value = value;
        this.setDeletable(true);
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

    public boolean isDeletable() {
        return this.isDeletable;
    }

    @JsonProperty("isDeletable")
    public void setDeletable(boolean deletable) {
        this.isDeletable = deletable;
    }
}
