package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.TaskDefinition;
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
    private EntityPermissionTypeService entityPermissionTypeService;

    public PipelineDefinitionAuthorizationService() {
        this.pipelineDefinitionService = new PipelineDefinitionService();
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.entityPermissionTypeService = new EntityPermissionTypeService();
    }

    public PipelineDefinitionAuthorizationService(IPipelineDefinitionService pipelineDefinitionService) {
        this.pipelineDefinitionService = pipelineDefinitionService;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
        this.entityPermissionTypeService = new EntityPermissionTypeService(this.pipelineDefinitionService);
    }

    @Override
    public List<DbEntry> getAll(List permissions, List pipelineDefinitions) {
        List<DbEntry> result = new ArrayList<>();
        for (PipelineDefinition pipelineDefinition : (List<PipelineDefinition>) pipelineDefinitions) {
            if (this.hasPermissionToRead(permissions, pipelineDefinition)) {
                pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);
                result.add(pipelineDefinition);
            }
        }
        return result;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(entityId).getObject();
        pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);
        return this.hasPermissionToRead(permissions, pipelineDefinition);
    }

    @Override
    public boolean delete(String entityId, List permissions) {
        PipelineDefinition pipelineDefinition = (PipelineDefinition) this.pipelineDefinitionService.getById(entityId).getObject();

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineDefinition);
    }

    @Override
    public boolean update(String entity, List permissions) {
        PipelineDefinition pipelineDefinition = this.jsonConverter.fromJson(entity, PipelineDefinition.class);
        pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);

        return this.hasPermissionToUpdateAndDelete(permissions, pipelineDefinition);
    }

    @Override
    public boolean add(String entity, List permissions) {
        PipelineDefinition pipelineDefinition = this.jsonConverter.fromJson(entity, PipelineDefinition.class);
        pipelineDefinition = this.entityPermissionTypeService.setPermissionTypeToObject(permissions, pipelineDefinition);

        return this.hasPermissionToAdd(permissions, pipelineDefinition);
    }

    public boolean assignUnassign(List<Permission> permissions) {
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasPermissionToRead(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() != PermissionType.NONE)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() != PermissionType.NONE)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                if (permission.getPermissionType() == PermissionType.NONE) {
                    hasPermission = false;
                } else {
                    hasPermission = true;
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
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

    private boolean hasPermissionToAdd(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    if (permission.getPermissionType() == PermissionType.ADMIN) {
                        hasPermission = true;
                    } else {
                        hasPermission = false;
                    }
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                if (permission.getPermissionType() == PermissionType.ADMIN) {
                    hasPermission = true;
                    return hasPermission;
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }

    private boolean hasPermissionToUpdateAndDelete(List<Permission> permissions, PipelineDefinition pipelineDefinition) {
        boolean hasPermission = false;
        for (Permission permission : permissions) {
            if ((permission.getPermissionScope() == PermissionScope.SERVER) && (permission.getPermissionType() == PermissionType.ADMIN)) {
                hasPermission = true;
            } else if (permission.getPermittedEntityId().equals(PermissionScope.PIPELINE.toString()) || permission.getPermittedEntityId().equals(PermissionScope.PIPELINE_GROUP.toString())) {
                if ((permission.getPermissionType() == PermissionType.ADMIN)) {
                    hasPermission = true;
                } else {
                    hasPermission = false;
                }
            } else if (pipelineDefinition.getPipelineGroupId() != null) {
                if (permission.getPermittedEntityId().equals(pipelineDefinition.getPipelineGroupId())) {
                    if (permission.getPermissionType() == PermissionType.ADMIN) {
                        hasPermission = true;
                    } else {
                        hasPermission = false;
                    }
                }
            }
            if (permission.getPermittedEntityId().equals(pipelineDefinition.getId())) {
                if (permission.getPermissionType() == PermissionType.ADMIN) {
                    hasPermission = true;
                    return hasPermission;
                } else {
                    hasPermission = false;
                }
            }
        }
        return hasPermission;
    }
}
