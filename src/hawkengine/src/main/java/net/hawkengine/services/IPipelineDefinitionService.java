//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

public interface IPipelineDefinitionService extends ICrudService<PipelineDefinition> {
    ServiceResult getById(String pipelineDefinitionId);

    ServiceResult getAll();

    ServiceResult add(PipelineDefinition pipelineDefinition);

    ServiceResult update(PipelineDefinition pipelineDefinition);

    ServiceResult delete(String pipelineDefinitionId);
}
