
package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.ExecutionState;
import net.hawkengine.model.PipelineDefinition;

public interface IPipelineExecutionService extends IDbRepository<PipelineDefinition> {
	ArrayList<PipelineDefinition> getPpipelinesByStatus(ExecutionState status) throws Exception;

	int getLastRunIDForPipeline(String pipelineName) throws Exception;

	PipelineDefinition getPipelineExecutionByExecutionID(String pipelineName, int pipelineExecutionID) throws Exception;

}
