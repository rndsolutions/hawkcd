package io.hawkcd.services;

import io.hawkcd.model.PipelineDefinition;
import io.hawkcd.model.PipelineGroup;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.interfaces.IPipelineDefinitionService;
import io.hawkcd.services.interfaces.IPipelineGroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts the business service and exposes an interface to the UI
 */
public class DtoService {
    private IPipelineGroupService pipelineGroupService;
    private IPipelineDefinitionService pipelineDefinitionService;
    private User currentUser;

    public DtoService(User currentUser) {
        this.currentUser = currentUser;
        this.pipelineGroupService = new PipelineGroupService();
        this.pipelineDefinitionService = new PipelineDefinitionService();
    }

    public ServiceResult getAllPipelineGroupDTOs() {
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.pipelineGroupService.getAll().getEntity();
        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) pipelineDefinitionService.getAll().getEntity();

        for (PipelineGroup pipelineGroup : pipelineGroups) {
            List<PipelineDefinition> pipelineDefinitionsToAdd = new ArrayList<>();
            for (PipelineDefinition pipelineDefinition : pipelineDefinitions) {
                if (!pipelineDefinition.getPipelineGroupId().isEmpty() && pipelineDefinition.getPipelineGroupId().equals(pipelineGroup.getId())) {
                    pipelineDefinitionsToAdd.add(pipelineDefinition);
                }
            }

            pipelineGroup.setPipelines(pipelineDefinitionsToAdd);
        }

        ServiceResult result = new ServiceResult(pipelineGroups, NotificationType.SUCCESS, "All Pipeline Groups retrieved successfully.");

        return result;
    }
}
