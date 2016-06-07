//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

public interface IFileManagementService {
	String[] getFiles(String path) throws Exception;

	String zipItems(String zipFileFullPath, String sourcePath) throws Exception;

	String zipItems(String zipFilePath, String zipFileName, String sourcePath) throws Exception;

	String zipFilesAndPreserveStructure(String zipFileFullPath, String[] files, String root) throws Exception;

	void unzipFile(String zipFileFullPath, String unpackDirectory) throws Exception;

	double getCurrentDriveAvailableFreeSpaceInGb() throws Exception;

}
