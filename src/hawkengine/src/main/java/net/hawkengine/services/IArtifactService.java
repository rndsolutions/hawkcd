//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.List;

import net.hawkengine.model.Pipeline;

public interface IArtifactService {
	String[] getFiles(String path);

	String zipItems(String zipFileFullPath, String sourcePath);

	String zipItems(String zipFilePath, String zipFileName, String sourcePath);

	String zipFilesAndPreserveStructure(String zipFileFullPath, String[] files, String root);

	void unzipFile(String zipFileFullPath, String unpackDirectory);

	String resolvePath(String... args);

	String listArtifacts(String pipelineName, String pipelineId, String stageName, String stageId, String jobName)
			;

	void purgeArtifacts(List<Pipeline> pipelines, Double freeSpaceNeeded);

}
