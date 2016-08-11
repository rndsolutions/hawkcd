package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.enums.PermissionScope;
import net.hawkengine.model.enums.PermissionType;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.interfaces.IUserService;

import java.util.ArrayList;
import java.util.List;


public class UserService extends CrudService<User> implements IUserService {

    private static final Class CLASS_TYPE = User.class;

    public UserService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public UserService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    public ServiceResult getById(String id) {
        return super.getById(id);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(User object) {
        ServiceResult result = this.getByEmail(object.getEmail());
        if (result.hasError()){
            return result;
        }
        return super.add(object);
    }

    @Override
    public ServiceResult update(User object) {
        return super.update(object);
    }

    @Override
    public ServiceResult delete(String id) {
        return super.delete(id);
    }

    @Override
    public ServiceResult getByEmailAndPassword(String email, String password) {
        List<User> users = (List<User>) this.getAll().getObject();

        User user = users
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return super.createServiceResult(user, true, "'s email and password doesn't match");
        } else {
            return super.createServiceResult(user, false, "retrieved successfully");
        }
    }

    @Override
    public ServiceResult getByEmail(String email) {
        List<User> users = (List<User>) this.getAll().getObject();

        User user = users
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (user != null) {
            return super.createServiceResult(user, true, "with this email already exists");
        } else {
            return super.createServiceResult(user, false, "does not exist");
        }
    }

    @Override
    public ServiceResult addAdminServerUser() {
        User adminUser = new User();
        adminUser.setEmail("admin@admin.com");
        adminUser.setPassword("admin");
        Permission adminUserPermission = new Permission();
        adminUserPermission.setPermittedEntityId("SERVER");
        adminUserPermission.setPermissionType(PermissionType.ADMIN);
        adminUserPermission.setPermissionScope(PermissionScope.SERVER);
        List<Permission> permissions = new ArrayList<>();
        permissions.add(adminUserPermission);

        adminUser.setPermissions(permissions);

        return this.add(adminUser);
    }

    @Override
    public ServiceResult addUserWithoutProvider(User user) {
        return  this.add(user);
    }
}
