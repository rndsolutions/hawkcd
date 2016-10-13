package net.hawkengine.services;

import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.services.interfaces.IUserService;
import net.hawkengine.ws.SessionPool;
import org.apache.commons.codec.digest.DigestUtils;

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
    public ServiceResult getById(String userId) {
        return super.getById(userId);
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(User user) {
        ServiceResult result = this.getByEmail(user.getEmail());
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }
        String password = user.getPassword();
        String hashedPassword = DigestUtils.sha256Hex(password);
        user.setPassword(hashedPassword);
        return super.add(user);
    }

    @Override
    public ServiceResult update(User user) {
        ServiceResult serviceResult = super.update(user);
        SessionPool.getInstance().updateUserObjects(user.getId());
        return serviceResult;
    }

    @Override
    public ServiceResult delete(String userId) {
        return super.delete(userId);
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
            return super.createServiceResult(user, NotificationType.ERROR, "'s email and password doesn't match");
        } else {
            return super.createServiceResult(user, NotificationType.SUCCESS, "retrieved successfully");
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
            return super.createServiceResult(user, NotificationType.ERROR, "with this email already exists");
        } else {
            return super.createServiceResult(user, NotificationType.SUCCESS, "does not exist");
        }
    }

    @Override
    public ServiceResult addUserWithoutProvider(User user) {
        return this.add(user);
    }

    @Override
    public ServiceResult changeUserPassword(UserDto user, String newPasword, String oldPassword) {
        String hashedPassword = DigestUtils.sha256Hex(oldPassword);
        ServiceResult result = this.getByEmailAndPassword(user.getUsername(), hashedPassword);

        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }
        User userToUpdate = (User) result.getObject();
        String hashedPasswordToUpdateUser = DigestUtils.sha256Hex(newPasword);
        userToUpdate.setPassword(hashedPasswordToUpdateUser);

        return this.update(userToUpdate);
    }

    @Override
    public ServiceResult resetUserPassword(User user) {
        String hashedPassword = DigestUtils.sha256Hex(user.getPassword());

        ServiceResult result = this.getById(user.getId());

        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }
        user.setPassword(hashedPassword);
        return this.update(user);
    }
}
