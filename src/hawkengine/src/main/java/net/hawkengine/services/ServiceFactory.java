package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Pipeline;

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
	public IConfigService getConfigService(IDbRepository<Pipeline> pipelineRepository) {
		// TODO Auto-generated method stub
		return null;
	}
}
