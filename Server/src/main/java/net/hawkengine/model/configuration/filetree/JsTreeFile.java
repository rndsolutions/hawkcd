package net.hawkengine.model.configuration.filetree;

import java.util.List;

public class JsTreeFile {
    private String text;
    private List<JsTreeFile> children;
    private String path;

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<JsTreeFile> getChildren() {
        return this.children;
    }

    public void setChildren(List<JsTreeFile> children) {
        this.children = children;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
