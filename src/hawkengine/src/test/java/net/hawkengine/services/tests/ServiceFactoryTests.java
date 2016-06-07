package net.hawkengine.services.tests;

import net.hawkengine.model.PipelineGroup;
import net.hawkengine.services.ServiceFactory;
import org.junit.Test;

import net.hawkengine.services.ConfigService;

public class ServiceFactoryTests {
	
	@Test
	public void getConfigServiceDefault() throws Exception{
		
		ServiceFactory factory =  new ServiceFactory();
		
		ConfigService sf =  (ConfigService) factory.getConfigService();
		
		PipelineGroup tupan = sf.getPipelineGroup("tupan");
		
	}	
}
