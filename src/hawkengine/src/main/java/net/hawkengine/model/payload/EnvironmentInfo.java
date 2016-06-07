//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

public class EnvironmentInfo {
	private String __OsName;

	public String getOsName() {
		return __OsName;
	}

	public void setOsName(String value) {
		__OsName = value;
	}

	private String __OsVersion;

	public String getOsVersion() {
		return __OsVersion;
	}

	public void setOsVersion(String value) {
		__OsVersion = value;
	}

	private String __HostName;

	public String getHostName() {
		return __HostName;
	}

	public void setHostName(String value) {
		__HostName = value;
	}

	private double __TotalRamMBytes;

	public double getTotalRamMBytes() {
		return __TotalRamMBytes;
	}

	public void setTotalRamMBytes(double value) {
		__TotalRamMBytes = value;
	}

	private double __FreeRamMBytes;

	public double getFreeRamMBytes() {
		return __FreeRamMBytes;
	}

	public void setFreeRamMBytes(double value) {
		__FreeRamMBytes = value;
	}

	private long __TotalDiskSpaceGBytes;

	public long getTotalDiskSpaceGBytes() {
		return __TotalDiskSpaceGBytes;
	}

	public void setTotalDiskSpaceGBytes(long value) {
		__TotalDiskSpaceGBytes = value;
	}

	private long __FreeDiskSpaceGBytes;

	public long getFreeDiskSpaceGBytes() {
		return __FreeDiskSpaceGBytes;
	}

	public void setFreeDiskSpaceGBytes(long value) {
		__FreeDiskSpaceGBytes = value;
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