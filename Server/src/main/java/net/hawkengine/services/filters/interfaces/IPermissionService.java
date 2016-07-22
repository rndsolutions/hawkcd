package net.hawkengine.services.filters.interfaces;

import net.hawkengine.model.User;
import net.hawkengine.model.payload.Permission;

import javax.jws.soap.SOAPBinding;
import java.util.List;

public interface IPermissionService {
    List<Permission> distributePipelineGroupPermissions(Permission permission, User iser);

    List<Permission> distributeServerPermissions(Permission permission, User user);

    List<Permission> distributePipelinePermissions(Permission permission, User user);

    List<Permission> distributeEnvironmentPermissions(Permission permission, User user);
}
