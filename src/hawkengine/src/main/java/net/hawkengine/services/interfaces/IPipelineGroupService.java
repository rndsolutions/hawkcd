//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services.interfaces;

import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;

public interface IPipelineGroupService extends ICrudService<PipelineGroup>{
	@Override
	ServiceResult getById(String pipelineGroupId);

	@Override
	ServiceResult getAll();

	@Override
	ServiceResult add(PipelineGroup pipelineGroup);

	@Override
	ServiceResult update(PipelineGroup pipelineGroup);

	@Override
	ServiceResult delete(String pipelineGroupId);

	ServiceResult getAllPipelineGroupDTOs();
}
