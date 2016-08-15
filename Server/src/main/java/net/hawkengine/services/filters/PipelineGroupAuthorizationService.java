package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.PipelineGroupService;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.interfaces.IPipelineGroupService;

import java.util.ArrayList;
import java.util.List;

public class PipelineGroupAuthorizationService implements IAuthorizationService {
    private Gson jsonConverter;
    private IPipelineGroupService pipelineGroupService;

    public PipelineGroupAuthorizationService() {
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.pipelineGroupService = new PipelineGroupService();
    }

    public PipelineGroupAuthorizationService(IPipelineGroupService pipelineGroupService){
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.pipelineGroupService = pipelineGroupService;
    }


    @Override
    public List getAll(List permissions, List pipelineGroups) {
        List<PipelineGroup> result = new ArrayList<>();
        for (PipelineGroup pipelineGroup : (List<PipelineGroup>) pipelineGroups) {
            if (this.hasPermissionToRead(permissions, pipelineGroup)) {
                pipelineGroup = EntityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);
                result.add(pipelineGroup);
            }
        }
        return result;
    }

    @Override
    public boolean getById(String entitId, List permissions) {
        PipelineGroup pipelineGroup = (PipelineGroup)this.pipelineGroupService.getById(entitId).getObject();
        pipelineGroup = EntityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);

        return this.hasPermissionToRead(permissions, pipelineGroup);
    }

    @Override
    public boolean add(String entity, List permissions) {
        PipelineGroup pipelineGroup = this.jsonConverter.fromJson(entity, PipelineGroup.class);
        pipelineGroup = EntityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);

        return this.hasPermissionToAdd(permissions, pipelineGroup.getId());
    }

    @Override
    public boolean update(String entity, List permissions) {
        PipelineGroup pipelineGroup = this.jsonConverter.fromJson(entity, PipelineGroup.class);
        pipelineGroup = EntityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineGroup);

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineGroup.getId());
    }

    @Override
    public boolean delete(String entityId, List permissions) {

        return this.hasPermissionToUpdateAndDelete(permissions, entityId);
    }

    private boolean hasPermissionToRead(List<Permission> permissions, PipelineGroup pipelineGroup) {
        String pipelineGroupId = pipelineGroup.getId();
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.NONE)) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                }
            } else if (permission.getPermittedEntityId().equals(pipelineGroupId)) {
                if (permission.getPermissionType() == PermissionType.NONE) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                    return hasPermission;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasPermissionToAdd(List<Permission> permissions, String pipelineGroupId) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                    return hasPermission;
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasPermissionToUpdateAndDelete(List<Permission> permissions, String pipelineGroupId) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (permission.getPermittedEntityId().equals(pipelineGroupId)) {
                if (permission.getPermissionType() != PermissionType.ADMIN) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                    return hasPermission;
                }
            }
        }
        return hasPermission;
    }
}
