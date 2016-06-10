//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services.interfaces;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

public interface IPipelineDefinitionService extends ICrudService<PipelineDefinition> {
    @Override
    ServiceResult getById(String pipelineDefinitionId);

    @Override
    ServiceResult getAll();

    @Override
    ServiceResult add(PipelineDefinition pipelineDefinition);

    @Override
    ServiceResult update(PipelineDefinition pipelineDefinition);

    @Override
    ServiceResult delete(String pipelineDefinitionId);
}
