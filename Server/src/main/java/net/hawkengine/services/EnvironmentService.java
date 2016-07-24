package net.hawkengine.services;

import net.hawkengine.core.utilities.constants.ConfigurationConstants;
import net.hawkengine.db.DbRepositoryFactory;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Environment;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.IEnvironmentService;

public class EnvironmentService extends CrudService<Environment> implements IEnvironmentService {
    private static final Class CLASS_TYPE = Environment.class;

    public EnvironmentService() {
        IDbRepository repository = DbRepositoryFactory.create(ConfigurationConstants.DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public EnvironmentService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
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
