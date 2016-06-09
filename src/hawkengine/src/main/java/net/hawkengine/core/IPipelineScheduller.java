//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.core;

import net.hawkengine.model.Pipeline;
import net.hawkengine.services.IConfigService;
import net.hawkengine.services.IPipelineExecutionService;

import java.util.ArrayList;
import net.hawkengine.model.MaterialChange;

public interface IPipelineScheduller extends IServerComponent {
	IPipelineExecutionService getPipelineService() throws Exception;

	void setPipelineService(IPipelineExecutionService value) throws Exception;

	IConfigService getConfigService() throws Exception;

	void setConfigService(IConfigService value) throws Exception;

	void startPipeline(String pipelineId) throws Exception;

	void startPipeline(String pipelineName, ArrayList<MaterialChange> materialChanges) throws Exception;

	void startPipeline(Pipeline pipelineToStart) throws Exception;

}
