package net.hawkengine.model.configuration.filetree;

import java.util.List;

public class Directory {
    private String directoryName;
    private List<File> files;
    private List<Directory> subDirectories;

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public List<File> getFiles() {
        return this.files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Directory> getSubDirectories() {
        return this.subDirectories;
    }

    public void setSubDirectories(List<Directory> subDirectories) {
        this.subDirectories = subDirectories;
    }


}
