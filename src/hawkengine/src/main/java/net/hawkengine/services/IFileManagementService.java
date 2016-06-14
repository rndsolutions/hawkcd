//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

public interface IFileManagementService {
	String[] getFiles(String path);

	String zipItems(String zipFileFullPath, String sourcePath);

	String zipItems(String zipFilePath, String zipFileName, String sourcePath);

	String zipFilesAndPreserveStructure(String zipFileFullPath, String[] files, String root);

	void unzipFile(String zipFileFullPath, String unpackDirectory);

	double getCurrentDriveAvailableFreeSpaceInGb();

}
