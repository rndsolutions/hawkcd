package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.PipelineDefinition;

public class ServiceFactory implements IServiceFactory {

	private IConfigService configService;

	public ServiceFactory() {

	}

	@Override
	public IConfigService getConfigService() {
		this.configService = new ConfigService();
		return configService;
	}

	@Override
	public IConfigService getConfigService(IDbRepository<PipelineDefinition> pipelineRepository) {
		// TODO Auto-generated method stub
		return null;
	}
}
