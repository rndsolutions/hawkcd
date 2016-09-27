package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.PipelineDto;
import net.hawkengine.model.dto.PipelineGroupDto;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineGroupService;
import net.hawkengine.services.filters.factories.AuthorizationServiceFactory;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.filters.interfaces.ISecurityService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineGroupService;
import net.hawkengine.services.interfaces.IUserGroupService;
import net.hawkengine.ws.WsObjectProcessor;

import java.util.ArrayList;
import java.util.List;

public class SecurityService<T extends DbEntry> implements ISecurityService {
    private IAuthorizationService authorizationService;
    private Gson jsonConverter;
    private IPipelineGroupService pipelineGroupService;
    private IPipelineDefinitionService pipelineDefinitionService;

    public SecurityService() {
        this.pipelineGroupService = new PipelineGroupService();
        this.pipelineDefinitionService = new PipelineDefinitionService();

        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    public SecurityService(WsObjectProcessor wsObjectProcessor, IPipelineDefinitionService pipelineDefinitionService, IUserGroupService userGroupService, IPipelineGroupService pipelineGroupService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    @Override
    public List getAll(List entitiesToFilter, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        List<T> filteredEntities = this.authorizationService.getAll(permissions, entitiesToFilter);

        return filteredEntities;
    }

    @Override
    public List getAllPipelineGroupDTOs(List entitiesToFilter, String className, List permissions) {
        List<PipelineGroup> pipelineGroups = (List<PipelineGroup>) entitiesToFilter;
        this.authorizationService = AuthorizationServiceFactory.create(className);
        List<PipelineGroup> filteredPipelineGroups = (List<PipelineGroup>) this.authorizationService.getAll(permissions, pipelineGroups);

        List<PipelineDefinition> pipelineDefinitions = (List<PipelineDefinition>) this.pipelineDefinitionService.getAll().getObject();
        this.authorizationService = AuthorizationServiceFactory.create("PipelineDefinitionService");
        List<PipelineDefinition> filteredPipelineDefinitions = (List<PipelineDefinition>) this.authorizationService.getAll(permissions, pipelineDefinitions);

        filteredPipelineGroups = this.pipelineGroupService.placePipelinesIntoGroups(filteredPipelineGroups, filteredPipelineDefinitions);

        List<PipelineGroupDto> pipelineGroupDtos = this.pipelineGroupService.convertGroupsToDtos(filteredPipelineGroups);

        return pipelineGroupDtos;
    }

    @Override
    public List getAllUserGroups(List entitiesToFilter, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        List<T> filteredEntities = this.authorizationService.getAll(permissions, entitiesToFilter);

        return filteredEntities;
    }

    @Override
    public List getAllPipelineDtos(List entitiesToFilter, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        List<PipelineDto> filteredEntities = new ArrayList<>();
        for (Object entity : entitiesToFilter) {
            PipelineDto pipelineDto = (PipelineDto)entity;
            boolean hasPermission = this.authorizationService.getById(pipelineDto.getId(), permissions);
            if (hasPermission) {
                filteredEntities.add(pipelineDto);
            }
        }

        return filteredEntities;
    }

    @Override
    public boolean getById(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        String entityId = entity.substring(1, entity.length() - 1);
        boolean hasPermission = this.authorizationService.getById(entityId, permissions);
        if (hasPermission) {
            return hasPermission;
        }
        return false;
    }

    @Override
    public boolean add(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        boolean hasPermission = this.authorizationService.add(entity, permissions);
        if (hasPermission) {
            return true;
        }

        return false;
    }

    @Override
    public boolean update(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        boolean hasPermission = this.authorizationService.update(entity, permissions);
        if (hasPermission) {
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        String entityId = entity.substring(1, entity.length() - 1);
        boolean hasPermisssion = this.authorizationService.delete(entityId, permissions);
        if (hasPermisssion) {
            return true;
        }

        return false;
    }

    @Override
    public boolean addUserGroupDto(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        boolean hasPermission = this.authorizationService.add(entity, permissions);
        if (hasPermission) {
            return true;
        }

        return false;
    }

    @Override
    public boolean updateUserGroupDto(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        boolean hasPermission = this.authorizationService.update(entity, permissions);
        if (hasPermission) {
            return true;
        }

        return false;
    }

    @Override
    public boolean assignUserToGroup(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        UserGroup userGroup = this.jsonConverter.fromJson(entity, UserGroup.class);
        boolean hasPermission = this.authorizationService.getById(userGroup.getId(), permissions);
        if (hasPermission) {
            hasPermission = this.authorizationService.update(entity, permissions);
            if (hasPermission) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean unassignUserFromGroup(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        UserGroup userGroup = this.jsonConverter.fromJson(entity, UserGroup.class);
        boolean hasPermission = this.authorizationService.getById(userGroup.getId(), permissions);
        if (hasPermission) {
            hasPermission = this.authorizationService.update(entity, permissions);
            if (hasPermission) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean assignPipelineToGroup(String pipelineGroup, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        boolean hasPermission = true; //this.authorizationService.update(pipelineDefintion, permissions);
        if (hasPermission) {
            this.authorizationService = AuthorizationServiceFactory.create("PipelineGroupService");
            hasPermission = this.authorizationService.update(pipelineGroup, permissions);
            if (hasPermission) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean unassignPipelineFromGroup(String pipelineGroup, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        boolean hasPermission = this.authorizationService.update(pipelineGroup, permissions);
        if (hasPermission) {
            return true;
        }

        return false;
    }

    @Override
    public boolean addUserWithoutProvider(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        boolean hasPermission = this.authorizationService.add(entity, permissions);
        if (hasPermission) {
            return true;
        }

        return false;
    }

    @Override
    public boolean changeUserPassword(String loggedUserEmail, String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        UserDto userToUpdate = this.jsonConverter.fromJson(entity, UserDto.class);
        if (userToUpdate.getUsername().equals(loggedUserEmail)){
            return true;
        }
        boolean hasPermission = this.authorizationService.update(entity, permissions);

        return hasPermission;
    }

    @Override
    public boolean addWithMaterialDefinition(String entity, String className, List list) {
        return true;
    }

    @Override
    public boolean resetUserPassword(String entity, String className, List permissions) {
        this.authorizationService = AuthorizationServiceFactory.create(className);
        User user = this.jsonConverter.fromJson(entity, User.class);
        boolean hasPermission = this.authorizationService.update(entity, permissions);

        return hasPermission;
    }
}