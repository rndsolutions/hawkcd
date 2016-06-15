package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Environment;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IEnvironmentService;

public class EnvironmentService extends CrudService<Environment> implements IEnvironmentService {
    public EnvironmentService() {
        super.setRepository(new RedisRepository(Environment.class));
        super.setObjectType("Environment");
    }

    public EnvironmentService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType("Environment");
    }

    @Override
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    public ServiceResult add(Environment environment) {
        return super.add(environment);
    }

    @Override
    public ServiceResult delete(String environmentId) {
        return super.delete(environmentId);
    }

    @Override
    public ServiceResult update(Environment environment) {
        return super.update(environment);
    }
}
