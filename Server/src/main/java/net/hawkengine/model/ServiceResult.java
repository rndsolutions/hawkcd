package net.hawkengine.model;

public class ServiceResult {
    private boolean error;
    private String message;
    private Object object;

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean hasError() {
        return this.error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
