package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Environment;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IEnvironmentService;

public class EnvironmentService extends CrudService<Environment> implements IEnvironmentService {
    public EnvironmentService() {
        super.repository = new RedisRepository(Environment.class);
        super.objectType = "Environment";
    }

    public EnvironmentService(IDbRepository repository) {
        super.repository = repository;
        super.objectType = "Environment";
    }

    @Override
    public ServiceResult getAllEnvironments() {
        return super.getAll();
    }

    @Override
    public ServiceResult addEnvironment(Environment environment) {
        return super.add(environment);
    }

    @Override
    public ServiceResult deleteEnvironment(String environmentId) {
        return super.delete(environmentId);
    }

    @Override
    public ServiceResult updateEnvironment(Environment environment) {
        return super.update(environment);
    }
}
