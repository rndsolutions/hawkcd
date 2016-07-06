package net.hawkengine.services.interfaces;

import net.hawkengine.model.*;

public interface ITaskDefinitionService extends ICrudService<TaskDefinition> {
    ServiceResult add(ExecTask taskDefinition);

    ServiceResult add(UploadArtifactTask taskDefinition);

    ServiceResult add(FetchMaterialTask taskDefinition);

    ServiceResult add(FetchArtifactTask taskDefinition);

    ServiceResult update(ExecTask taskDefintion);

    ServiceResult update(UploadArtifactTask taskDefintion);

    ServiceResult update(FetchMaterialTask taskDefintion);

    ServiceResult update(FetchArtifactTask taskDefintion);
}
