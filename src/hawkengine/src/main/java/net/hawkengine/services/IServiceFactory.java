package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Pipeline;

public interface IServiceFactory {
	
	IConfigService getConfigService();
	
	IConfigService getConfigService(IDbRepository<Pipeline> pipelineRepository);
	
}
