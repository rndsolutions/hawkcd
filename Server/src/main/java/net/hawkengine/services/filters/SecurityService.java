package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.ConversionObject;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.factories.AuthorizationFactory;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.filters.interfaces.ISecurityService;
import net.hawkengine.ws.WsEndpoint;
import net.hawkengine.ws.WsObjectProcessor;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.ArrayList;
import java.util.List;

public class SecurityService<T extends DbEntry> implements ISecurityService {
    private static final Logger LOGGER = Logger.getLogger(WsEndpoint.class.getClass());
    private WsObjectProcessor wsObjectProcessor;
    private ServiceResult result;
    private IAuthorizationService authorizationService;
    private AuthorizationFactory authorizationFactory;
    private Gson jsonConverter;

    public SecurityService() {
        this.wsObjectProcessor = new WsObjectProcessor();
        this.authorizationFactory = new AuthorizationFactory();

        this.result = new ServiceResult();
        this.result.setError(true);
        this.result.setMessage("Unauthorized");
        this.result.setObject(null);

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }


    @Override
    public ServiceResult getAll(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            List<T> entitiesToFilter = (List<T>) this.result.getObject();
            this.authorizationService = this.authorizationFactory.filter(contract.getClassName());
            List<T> filteredEntities = this.authorizationService.getAll(permissions, entitiesToFilter);
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
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) pipelineDefinitionsServiceResult.getObject();
            List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.result.getObject();

            this.authorizationService = this.authorizationFactory.filter(contract.getClassName());
            //TODO: REFACTOR THIS PART
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
            this.authorizationService = this.authorizationFactory.filter(contract.getClassName());
            String entity = contract.getArgs()[0].getObject();

            //TODO: See why there are aditional quotes
            String entityId = entity.substring(1, entity.length()- 1);
            boolean hasPermisssion = this.authorizationService.getById(entityId, permissions);
            if (hasPermisssion) {
                this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            }

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult add(WsContractDto contract, List<Permission> permissions) {
        try {
            this.authorizationService = this.authorizationFactory.filter(contract.getClassName());
            String entity = contract.getArgs()[0].getObject();
            boolean hasPermission = this.authorizationService.add(entity, permissions);
            if (hasPermission) {
                this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return this.result;
    }

    @Override
    public ServiceResult update(WsContractDto contract, List<Permission> permissions) {
        try {
            this.authorizationService = this.authorizationFactory.filter(contract.getClassName());
            String entity = contract.getArgs()[0].getObject();
            boolean hasPermission = this.authorizationService.update(entity, permissions);
            if (hasPermission) {
                this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return this.result;
    }

    @Override
    public ServiceResult delete(WsContractDto contract, List<Permission> permissions) {
        try {
            this.authorizationService = this.authorizationFactory.filter(contract.getClassName());
            String entity = contract.getArgs()[0].getObject();

            //TODO: See why there are aditional quotes
            String entityId = entity.substring(1, entity.length()- 1);
            boolean hasPermisssion = this.authorizationService.delete(entityId, permissions);
            if (hasPermisssion) {
                this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return this.result;
    }
}
