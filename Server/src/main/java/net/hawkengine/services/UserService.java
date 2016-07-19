package net.hawkengine.services;

import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.services.interfaces.IUserService;


public class UserService extends CrudService<User> implements IUserService {

    public UserService(){

        super.setRepository(new RedisRepository(User.class));
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
}
