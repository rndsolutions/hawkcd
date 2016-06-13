package net.hawkengine.services.tests;

import net.hawkengine.model.PipelineGroup;
import net.hawkengine.services.ServiceFactory;
import org.junit.Test;

import net.hawkengine.services.ConfigService;

@SuppressWarnings("UnusedAssignment")
public class ServiceFactoryTests {
	
	@Test
	public void getConfigServiceDefault() {
		
		ServiceFactory factory =  new ServiceFactory();
		
		ConfigService sf =  (ConfigService) factory.getConfigService();

	}	
}
