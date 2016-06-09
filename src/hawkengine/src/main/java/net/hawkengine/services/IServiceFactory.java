package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.PipelineDefinition;

public interface IServiceFactory {
	
	IConfigService getConfigService();
	
	IConfigService getConfigService(IDbRepository<PipelineDefinition> pipelineRepository);
	
}
