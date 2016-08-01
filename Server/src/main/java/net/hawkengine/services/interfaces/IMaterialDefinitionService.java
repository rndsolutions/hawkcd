package net.hawkengine.services.interfaces;

import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.NugetMaterial;
import net.hawkengine.model.ServiceResult;

public interface IMaterialDefinitionService extends ICrudService<MaterialDefinition> {
    ServiceResult add(GitMaterial gitMaterial);

    ServiceResult add(NugetMaterial nugetMaterial);

    ServiceResult update(GitMaterial gitMaterial);

    ServiceResult update(NugetMaterial nugetMaterial);

    ServiceResult updateMaterialDefinition(MaterialDefinition materialDefinition);

    ServiceResult addMaterialDefinition(MaterialDefinition materialDefinition);
}
