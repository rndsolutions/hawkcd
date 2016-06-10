package net.hawkengine.services;


import net.hawkengine.db.IDbRepository;
import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.Environment;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;

public class EnvironmentService extends CrudService<Environment> implements IEnvironmentService {

    private IDbRepository<PipelineDefinition> repository;
    private ServiceResult serviceResult;

    public EnvironmentService() {
        super.repository = new RedisRepository(Environment.class);
        super.type = "Environment";
    }

    public EnvironmentService(IDbRepository repository) {
        super.repository = repository;
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
