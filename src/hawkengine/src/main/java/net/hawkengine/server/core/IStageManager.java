//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:14 PM
//

package net.hawkengine.server.core;

import java.util.ArrayList;

import net.hawkengine.model.Job;

public interface IStageManager {
	void runStage(String pipelineName, int pipelineExecutionID, String stageName) throws Exception;

	void runStage(String pipelineName, int pipelineExecutionID, String stageName, ArrayList<Job> jobsForExecution)
			throws Exception;
}
