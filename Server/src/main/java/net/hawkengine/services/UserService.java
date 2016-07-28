package net.hawkengine.services;

import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.services.interfaces.ICrudService;
import net.hawkengine.services.interfaces.IUserService;

import java.util.List;


public class UserService extends CrudService<User> implements IUserService {

    public UserService(){

        super.setRepository(new RedisRepository(User.class));
        super.setObjectType("User");
    }

    public  UserService(RedisRepository redisRepository){
        super.setRepository(redisRepository);
        super.setObjectType("User");
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

        if (user == null){
            return super.createServiceResult(user, true, "not found");
        }else{
            return super.createServiceResult(user, false, "retrieved successfully");
        }
    }

    @Override
    public ServiceResult addUserWithoutProvider(User user) {
        List<User> users = (List<User>) this.getAll().getObject();

        User userFromDb = users
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .orElse(null);

        if (userFromDb == null){
            this.add(user);
            return super.createServiceResult(user, false, "created successfully");
        }else{
            return super.createServiceResult(user, true, "already present");
        }
    }
}

