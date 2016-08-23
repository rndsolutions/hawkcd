package net.hawkengine.services.interfaces;

import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.ServiceResult;

public interface IMaterialDefinitionService extends ICrudService<MaterialDefinition> {
    ServiceResult getAllFromPipelineDefinition(String pipelineDefinitionId);

    ServiceResult add(GitMaterial gitMaterial);

    ServiceResult update(GitMaterial gitMaterial);
}
