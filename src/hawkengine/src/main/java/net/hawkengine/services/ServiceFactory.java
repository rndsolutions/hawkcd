package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Pipeline;

public class ServiceFactory implements IServiceFactory {

	IConfigService configService;

	@Override
	public IConfigService getConfigService() {
		this.configService = new ConfigService();
		return this.configService;
	}

	@Override
	public IConfigService getConfigService(IDbRepository<Pipeline> pipelineRepository) {
		// TODO Auto-generated method stub
		return null;
	}
}
