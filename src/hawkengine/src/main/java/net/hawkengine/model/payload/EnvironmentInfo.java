package net.hawkengine.model.payload;

public class EnvironmentInfo {
    private String osName;
    private String osVersion;
    private String hostName;
    private double totalRamMBytes;
    private double freeRamMBytes;
    private long totalDiskSpaceGBytes;
    private long freeDiskSpaceGBytes;

    public String getOsName() {
        return this.osName;
    }

    public void setOsName(String value) {
        this.osName = value;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public void setOsVersion(String value) {
        this.osVersion = value;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String value) {
        this.hostName = value;
    }

    public double getTotalRamMBytes() {
        return this.totalRamMBytes;
    }

    public void setTotalRamMBytes(double value) {
        this.totalRamMBytes = value;
    }

    public double getFreeRamMBytes() {
        return this.freeRamMBytes;
    }

    public void setFreeRamMBytes(double value) {
        this.freeRamMBytes = value;
    }

    public long getTotalDiskSpaceGBytes() {
        return this.totalDiskSpaceGBytes;
    }

    public void setTotalDiskSpaceGBytes(long value) {
        this.totalDiskSpaceGBytes = value;
    }

    public long getFreeDiskSpaceGBytes() {
        return this.freeDiskSpaceGBytes;
    }

    public void setFreeDiskSpaceGBytes(long value) {
        this.freeDiskSpaceGBytes = value;
    }
}