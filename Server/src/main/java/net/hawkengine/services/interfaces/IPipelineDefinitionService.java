//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services.interfaces;

import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;

public interface IPipelineDefinitionService extends ICrudService<PipelineDefinition> {
    ServiceResult getAllAutomaticallyScheduledPipelines();

    ServiceResult unassignPipelineFromGroup(PipelineDefinition pipelineDefinition);
}
