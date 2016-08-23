package net.hawkengine.services.interfaces;

import net.hawkengine.model.Material;
import net.hawkengine.model.ServiceResult;

public interface IMaterialService extends ICrudService<Material>{
    ServiceResult getAllFromPipelineDefinition(String pipelineDefinitionId);

    ServiceResult getLatestMaterial(String materialDefinitionId, String pipelineDefinitionId);
}
