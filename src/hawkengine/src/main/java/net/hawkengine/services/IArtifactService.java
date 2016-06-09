//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.List;

import net.hawkengine.model.PipelineDefinition;

public interface IArtifactService {
	String[] getFiles(String path) throws Exception;

	String zipItems(String zipFileFullPath, String sourcePath) throws Exception;

	String zipItems(String zipFilePath, String zipFileName, String sourcePath) throws Exception;

	String zipFilesAndPreserveStructure(String zipFileFullPath, String[] files, String root) throws Exception;

	void unzipFile(String zipFileFullPath, String unpackDirectory) throws Exception;

	String resolvePath(String... args) throws Exception;

	String listArtifacts(String pipelineName, String pipelineId, String stageName, String stageId, String jobName)
			throws Exception;

	void purgeArtifacts(List<PipelineDefinition> pipelineDefinitions, Double freeSpaceNeeded) throws Exception;

}
