package net.hawkengine.model;

public class ServiceResult {
    private Object object;
    private boolean error;
    private String message;

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
