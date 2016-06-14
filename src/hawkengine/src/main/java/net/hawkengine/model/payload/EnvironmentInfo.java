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

// public EnvironmentInfo()
// {
// // Init();
// }
// private void Init()
// {
// //Disk Space
// long totalSize = 0;
// long freeSpace = 0;
// foreach (DriveInfo drive in DriveInfo.GetDrives())
// {
// totalSize += drive.TotalSize;
// freeSpace += drive.TotalFreeSpace;
// }
// TotalDiskSpaceGBytes = totalSize / 1024 / 1024 / 1024;
// FreeDiskSpaceGBytes = freeSpace / 1024 / 1024 / 1024;
// //Ram
// TotalRamMBytes = GetTotalRam();
// FreeRamMBytes = GetFreeRam();
// this.HostName = System.Environment.MachineName;
// }
// public class MEMORYSTATUSEX
// {
// public uint dwLength;
// public uint dwMemoryLoad;
// public ulong ullTotalPhys;
// public ulong ullAvailPhys;
// public ulong ullTotalPageFile;
// public ulong ullAvailPageFile;
// public ulong ullTotalVirtual;
// public ulong ullAvailVirtual;
// public ulong ullAvailExtendedVirtual;
// public MEMORYSTATUSEX()
// {
// this.dwLength = (uint)Marshal.SizeOf(typeof(MEMORYSTATUSEX));
// }
// }
// [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
// public static extern bool GlobalMemoryStatusEx([In, Out] MEMORYSTATUSEX
// lpBuffer);
// public static ulong GetTotalRam()
// {
// MEMORYSTATUSEX mem = new MEMORYSTATUSEX();
// GlobalMemoryStatusEx(mem);
// return mem.ullTotalPhys;
// }
// public static ulong GetFreeRam()
// {
// MEMORYSTATUSEX mem = new MEMORYSTATUSEX();
// GlobalMemoryStatusEx(mem);
// return mem.ullAvailPhys;
// }
// }