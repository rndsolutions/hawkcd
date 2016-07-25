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
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.interfaces.IPipelineDefinitionService;
import net.hawkengine.services.interfaces.IPipelineService;

import java.util.ArrayList;
import java.util.List;

public class PipelineAuthorizationService implements IAuthorizationService {
    private IPipelineDefinitionService pipelineDefinitionService;
    private IPipelineService pipelineService;
    private Gson jsonConverter;

    public PipelineAuthorizationService(){
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.pipelineService = new PipelineService();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    @Override
    public List getAll(List permissions, List pipelines) {
        List<DbEntry> result = new ArrayList<>();
        for (Pipeline pipeline : (List<Pipeline>) pipelines) {
            if (this.hasPermissionToRead(permissions, pipeline)) {
                result.add(pipeline);
            }
        }
        return result;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        Pipeline pipeline = (Pipeline) this.pipelineDefinitionService.getById(entityId).getObject();

        return this.hasPermissionToRead(permissions, pipeline);
    }

    @Override
    public boolean add(String entity, List permissions) {
        Pipeline pipeline = this.jsonConverter.fromJson(entity, Pipeline.class);

        return this.hasPermissionToAdd(permissions, pipeline);
    }

    @Override
    public boolean update(String entity, List permissions) {
        Pipeline pipeline = this.jsonConverter.fromJson(entity, Pipeline.class);

        return this.hasPermissionToUpdateAndDelete(permissions, pipeline);
    }

    @Override
    public boolean delete(String entity, List permissions) {
        Pipeline pipeline = (Pipeline) this.pipelineService.getById(entity).getObject();

        return this.hasPermissionToUpdateAndDelete(permissions, pipeline);
    }

    private boolean hasPermissionToRead(List<Permission> permissions, Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        for (Permission permission : permissions) {
            if (permission.getPermittedEntityId().equals(pipeline.getPipelineDefinitionId()) ||
                    permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId()) ||
                    ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN))
                    ) {
                if (permission.isAbleToGet()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasPermissionToAdd(List<Permission> permissions, Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        for (Permission permission : permissions) {
            if (permission.getPermittedEntityId().equals(pipeline.getPipelineDefinitionId()) ||
                    permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId()) ||
                    ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN))) {
                if (permission.isAbleToAdd()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasPermissionToUpdateAndDelete(List<Permission> permissions, Pipeline pipeline) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(pipeline.getPipelineDefinitionId()).getObject();
        for (Permission permission : permissions) {
            if (permission.getPermittedEntityId().equals(pipeline.getPipelineDefinitionId()) ||
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
