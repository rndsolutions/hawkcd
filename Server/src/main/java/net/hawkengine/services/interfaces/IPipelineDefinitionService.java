package net.hawkengine.services.interfaces;

import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;

public interface IPipelineDefinitionService extends ICrudService<PipelineDefinition> {
    ServiceResult getAllAutomaticallyScheduledPipelines();

    ServiceResult add(PipelineDefinition pipelineDefinition, MaterialDefinition materialDefinition);

    ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, GitMaterial materialDefinition);

    ServiceResult addWithMaterialDefinition(PipelineDefinition pipelineDefinition, String materialDefinitionId);

    ServiceResult unassignPipelineFromGroup(String pipelineDefinitionId);

    ServiceResult assignPipelineToGroup(String pipelineDefinitionId, String pipelineGroupId, String pipelineGroupName);
}
