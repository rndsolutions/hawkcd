package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.*;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;

import java.util.ArrayList;
import java.util.List;

public class PipelineDefinitionAuthorizationService implements IAuthorizationService {
    private IPipelineDefinitionService pipelineDefinitionService;
    private Gson jsonConverter;

    public PipelineDefinitionAuthorizationService(){
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }
    @Override
    public List<DbEntry> getAll(List permissions, List pipelineDefinitions) {
        List<DbEntry> result = new ArrayList<>();
        for (PipelineDefinition pipelineDefinition : (List<PipelineDefinition>) pipelineDefinitions) {
            if (this.hasPermissionToRead(permissions, pipelineDefinition)) {
                result.add(pipelineDefinition);
            }
        }
        return result;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition)this.pipelineDefinitionService.getById(entityId).getObject();

        return this.hasPermissionToRead(permissions, pipelineDefinition);
    }

    @Override
    public boolean delete(String entity, List permissions) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(entity).getObject();

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineDefinition);
    }

    @Override
    public boolean update(String entity, List permissions) {
        PipelineDefinition pipelineDefinition = this.jsonConverter.fromJson(entity, PipelineDefinition.class);

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineDefinition);
    }

    @Override
    public boolean add(String entity, List permissions) {
        PipelineDefinition pipelineDefinition = this.jsonConverter.fromJson(entity, PipelineDefinition.class);

        return this.hasPermissionToAdd(permissions, pipelineDefinition);
    }

    private boolean hasPermissionToRead(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        for (Permission permission : permissions) {
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId()) ||
                    permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())  ||
                    ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN))) {
                if (permission.isAbleToGet()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasPermissionToAdd(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        for (Permission permission : permissions) {
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId()) ||
                    permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId()) ||
                    ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN))) {
                if (permission.isAbleToAdd()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasPermissionToUpdateAndDelete(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        for (Permission permission : permissions) {
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId()) ||
                    permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId()) ||
                    ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN))) {
                if ((permission.isAbleToUpdate() && permission.isAbleToDelete())) {
                    return true;
                }
            }
        }

        return false;
    }
}
