package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.UserGroupService;
import net.hawkengine.services.filters.factories.AuthorizationServiceFactory;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.filters.interfaces.ISecurityService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IUserGroupService;
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
    private AuthorizationServiceFactory authorizationServiceFactory;
    private IUserGroupService userGroupService;
    private Gson jsonConverter;
    private IPipelineDefinitionService pipelineDefinitionService;

    public SecurityService() {
        this.wsObjectProcessor = new WsObjectProcessor();
        this.authorizationServiceFactory = new AuthorizationServiceFactory();
        this.userGroupService = new UserGroupService();
        this.pipelineDefinitionService = new PipelineDefinitionService();

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

    public SecurityService(WsObjectProcessor wsObjectProcessor, IPipelineDefinitionService pipelineDefinitionService, IUserGroupService userGroupService){
        this.wsObjectProcessor = wsObjectProcessor;
        this.authorizationServiceFactory = new AuthorizationServiceFactory();
        this.userGroupService = userGroupService;
        this.pipelineDefinitionService = pipelineDefinitionService;

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
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
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
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
            List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) this.result.getObject();
            List<PipelineGroup> filteredPipelineGroups = new ArrayList<>();
            List<PipelineDefinition> pipelineDefinitionsToAdd = new ArrayList<>();
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
            //TODO: REFACTOR THIS PART
            List<PipelineGroup> filteredEntities = (List<PipelineGroup>) this.authorizationService.getAll(permissions, pipelineGroups);
            this.authorizationService = this.authorizationServiceFactory.create("PipelineDefinitionService");
            List<T> filteredPipelineDefintions = (List<T>) this.authorizationService.getAll(permissions, pipelineDefinitions);

            for (PipelineGroup filteredEntity:filteredEntities) {

                List<PipelineDefinition> pipelineDefinitionsWithinFilteredGroup = filteredEntity.getPipelines();
                for (PipelineDefinition pipelineDefinitionWithinFilteredGroup: pipelineDefinitionsWithinFilteredGroup) {
                    for (PipelineDefinition filteredPipelineDefintion:(List<PipelineDefinition>)filteredPipelineDefintions){
                        if (filteredPipelineDefintion.getId().equals(pipelineDefinitionWithinFilteredGroup.getId()) && filteredPipelineDefintion.getPipelineGroupId().equals(filteredEntity.getId())){
                            pipelineDefinitionsToAdd.add(filteredPipelineDefintion);
                        }
                    }
                }
                filteredEntity.setPipelines(pipelineDefinitionsToAdd);
                filteredPipelineGroups.add(filteredEntity);
                pipelineDefinitionsToAdd = new ArrayList<>();
            }
            this.result.setObject(filteredPipelineGroups);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult getById(WsContractDto contract, List<Permission> permissions) {
        try {
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
            String entity = contract.getArgs()[0].getObject();

            //TODO: See why there are aditional quotes
            String entityId = entity.substring(1, entity.length() - 1);
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
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
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
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
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
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
            String entity = contract.getArgs()[0].getObject();

            //TODO: See why there are aditional quotes
            String entityId = entity.substring(1, entity.length() - 1);
            boolean hasPermisssion = this.authorizationService.delete(entityId, permissions);
            if (hasPermisssion) {
                this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return this.result;
    }

    @Override
    public ServiceResult addUserToGroup(WsContractDto contract, List<Permission> permissions) {
        try {
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
            String group = contract.getArgs()[1].getObject();


            //TODO: See why there are aditional quotes
            String groupId = group.substring(1, group.length() - 1);

            boolean hasPermission = this.authorizationService.getById(groupId, permissions);
            if (hasPermission){
                UserGroup userGroup = (UserGroup)this.userGroupService.getById(groupId).getObject();
                String userGroupAsString = jsonConverter.toJson(userGroup);

                hasPermission = this.authorizationService.update(userGroupAsString, permissions);
                if (hasPermission) {
                    this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return this.result;
    }

    @Override
    public ServiceResult removeUserFromGroup(WsContractDto contract, List<Permission> permissions) {
        try {
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
            String group = contract.getArgs()[1].getObject();


            //TODO: See why there are aditional quotes
            String groupId = group.substring(1, group.length() - 1);

            boolean hasPermission = this.authorizationService.getById(groupId, permissions);
            if (hasPermission) {
                UserGroup userGroup = (UserGroup) this.userGroupService.getById(groupId).getObject();
                String userGroupAsString = jsonConverter.toJson(userGroup);

                hasPermission = this.authorizationService.update(userGroupAsString, permissions);
                if (hasPermission) {
                    this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return this.result;
    }

    @Override
    public ServiceResult getAllUserGroups(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            List<T> entitiesToFilter = (List<T>) this.result.getObject();
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
            List<T> filteredEntities = this.authorizationService.getAll(permissions, entitiesToFilter);
            this.result.setObject(filteredEntities);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult assignPipelineToGroup(WsContractDto contract, List<Permission> permissions) {
        this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
        String entity = contract.getArgs()[0].getObject();
        boolean hasPermission = this.authorizationService.update(entity, permissions);
        if (hasPermission) {
            entity = contract.getArgs()[1].getObject();
            this.authorizationService = this.authorizationServiceFactory.create("PipelineGroupService");
            hasPermission = this.authorizationService.delete(entity, permissions);
            if (hasPermission) {
                return this.update(contract, permissions);
            }
        }
        this.result.setError(true);
        this.result.setObject(null);
        this.result.setMessage("Unauthorized");
        return this.result;
    }

    @Override
    public ServiceResult unassignPipelineFromGroup(WsContractDto contract, List<Permission> permissions) {
        try {
            this.authorizationService = this.authorizationServiceFactory.create(contract.getClassName());
            String entity = contract.getArgs()[0].getObject();
            boolean hasPermission = this.authorizationService.update(entity, permissions);
            if (hasPermission) {
                PipelineDefinition pipelineDefinitionToUpdate = this.jsonConverter.fromJson(entity, PipelineDefinition.class);
                pipelineDefinitionToUpdate.setPipelineGroupId("");
                pipelineDefinitionToUpdate.setGroupName("");
                entity = this.jsonConverter.toJson(pipelineDefinitionToUpdate);
                contract.getArgs()[0].setObject(entity);
                this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return this.result;
    }
}
