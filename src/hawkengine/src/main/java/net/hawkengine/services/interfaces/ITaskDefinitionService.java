package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.TaskDefinition;

public interface ITaskDefinitionService {
    ServiceResult getById(String taskDefinitionId);

    ServiceResult getAll();

    ServiceResult add(TaskDefinition taskDefinition);

    ServiceResult update(TaskDefinition taskDefinition);

    ServiceResult delete(String taskDefinitionId);
}
