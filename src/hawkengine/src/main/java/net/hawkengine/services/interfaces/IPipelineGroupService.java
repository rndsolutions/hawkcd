//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services.interfaces;

import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

public interface IPipelineGroupService extends ICrudService<PipelineGroup>{
	ServiceResult getById(String pipelineGroupId);

	ServiceResult getAll();

	ServiceResult add(PipelineGroup pipelineGroup);

	ServiceResult update(PipelineGroup pipelineGroup);

	ServiceResult delete(String pipelineGroupId);

}
