package net.hawkengine.services.filters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.utilities.deserializers.MaterialDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.DbEntry;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;

import java.util.List;

public class AgentAuthorizationService implements IAuthorizationService{
    private Gson jsonConverter;

    public AgentAuthorizationService(){
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();
    }

    @Override
    public List getAll(List permissions, List entriesToFilter) {
       return entriesToFilter;
    }

    @Override
    public boolean getById(String entityId, List permissions) {
        return true;
    }

    @Override
    public boolean add(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    @Override
    public boolean update(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    @Override
    public boolean delete(String entity, List permissions) {
        return this.hasPermission(permissions);
    }

    private boolean hasPermission(List<Permission> permissions){
        for (Permission permission: permissions) {
            if (permission.getPermissionScope() == PermissionScope.SERVER && permission.getPermissionType() == PermissionType.ADMIN){
                return true;
            }
        }
        return false;
    }
}
