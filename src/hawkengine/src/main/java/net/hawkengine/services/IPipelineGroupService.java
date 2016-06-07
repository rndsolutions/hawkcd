//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;
import java.util.List;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.PipelineGroup;

public interface IPipelineGroupService {
	List<PipelineGroup> getAllPipelineGroups() throws Exception;

	PipelineGroup getPipelineGroup(String pipelineGroupName) throws Exception;

	PipelineGroup getPipelineGroupWithLatestPipelineExecution(String pipelineGroupName) throws Exception;

	String addPipelineGroup(PipelineGroup pipelineGroup) throws Exception;

	String updatePipelineGroup(String pipelineGroupName, PipelineGroup newPipelineGroup) throws Exception;

	String deletePipelineGroup(String pipelineGroupName) throws Exception;

}
