package net.hawkengine.ws.tests;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.ws.WsEndpoint;
import org.junit.Assert;

import org.junit.Test;

public class WsEndpointTest {

	@Test	
	public final void resolve_valid_json() {
		
		//arrange
		WsEndpoint wsep = new WsEndpoint();
		String message = "{ \"methodName\": \"getPipelineGroup\", \"className\":\"ConfigService\", \"packageName\": \"hawkengine.net.services\"}";
						
		//act	
		WsContractDto contract = wsep.resolve(message);
		
		//assert
		Assert.assertNotNull(contract);			
	}
	
	@Test
	public void call_existing_service() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		//arrange
		WsEndpoint ep = new WsEndpoint();
		
		WsContractDto contract = new WsContractDto();
		contract.setClassName("ConfigService");
		contract.setPackageName("hawkengine.net.services");
		contract.setMethodName("getPipelineGroup");
//		contract.args = new String [] {"param1"};
		
		try {
			addPath("/home/rado/gh/hawkengine/src/hawkengine/build/classes");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//act
	    Object result = ep.call(contract);		
			
		//assert
	    Assert.assertNotNull(result);
		
	}
	
	//need to do add path to Classpath with reflection since the URLClassLoader.addURL(URL url) method is protected:
	public static void addPath(String s) throws Exception {
	    File f = new File(s);
	    URI u = f.toURI();
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class<URLClassLoader> urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, new Object[]{u.toURL()});
	}


}
		