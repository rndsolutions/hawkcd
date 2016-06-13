package net.hawkengine.model;

import java.util.ArrayList;
import java.util.Date;

public class Agent extends DbEntry {
    private String name;
    private String hostName;
    private String ipAddress;
    private String rootPath;
    private Object os;
    private ArrayList<String> resources;
    private Environment environment;
    private boolean isRunning;
    private boolean isEnabled;
    private boolean isConnected;
    private Date lastReported;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public Object getOs() {
        return this.os;
    }

    public void setOs(Object os) {
        this.os = os;
    }

    public ArrayList<String> getResources() {
        return this.resources;
    }

    public void setResources(ArrayList<String> resources) {
        this.resources = resources;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    public Date getLastReported() {
        return this.lastReported;
    }

    public void setLastReported(Date lastReported) {
        this.lastReported = lastReported;
    }
}