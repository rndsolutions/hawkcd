package net.hawkengine.services.filters;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.filters.interfaces.ISecurityService;
import net.hawkengine.ws.WsEndpoint;
import net.hawkengine.ws.WsObjectProcessor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SecurityService<T extends DbEntry> implements ISecurityService {
    private static final Logger LOGGER = Logger.getLogger(WsEndpoint.class.getClass());
    private WsObjectProcessor wsObjectProcessor;
    private ServiceResult result;
    private IAuthorizationService authorizationService;

    public SecurityService() {
        this.wsObjectProcessor = new WsObjectProcessor();
        this.result = new ServiceResult();
        this.authorizationService = new AuthorizationService();
    }


    @Override
    public ServiceResult getAll(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            List<T> entitiesToFilter = (List<T>) this.result.getObject();
            List<T> filteredEntities = (List<T>) this.authorizationService.getAllPipelineDefinitions(permissions, (List<PipelineDefinition>) entitiesToFilter);

            this.result.setObject(filteredEntities);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult getPipelineDTOs(WsContractDto contract, List<Permission> permissions) {
        try {
        ServiceResult pipelineDefinitionsServiceResult = this.result;
//        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) pipelineDefinitionsServiceResult.getObject();
//        List<PipelineGroup> pipelineGroups = new ArrayList<>();
//        this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
//
//        for (PipelineDefinition pipelineDefinition: pipelineDefinitions){
//
//        }
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) pipelineDefinitionsServiceResult.getObject();
            List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.result.getObject();
            List<T> entitiesToFilter = new ArrayList<>();
            List<T> filteredEntities = (List<T>)this.authorizationService.getAll(permissions, pipelineGroups);
            for (PipelineDefinition pipelineDefinition: pipelineDefinitions) {
                PipelineGroup entityToAdd = pipelineGroups.stream().filter(g -> g.getId().equals(pipelineDefinition.getPipelineGroupId())).findFirst().orElse(null);
                boolean isFiltered = false;
                for (T filteredEntity: filteredEntities){
                    if(pipelineDefinition.getPipelineGroupId().equals(filteredEntity.getId())){
                        isFiltered = true;
                    }
                }
                if (!isFiltered && entityToAdd.getId().equals(pipelineDefinition.getPipelineGroupId())){
                    entityToAdd.setPipelines(new ArrayList<>());
                    entityToAdd.getPipelines().add(pipelineDefinition);
                    filteredEntities.add((T)entityToAdd);
                }
            }
            this.result.setObject(filteredEntities);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult getById(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult add(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult update(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult delete(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }
}
