package net.hawkengine.ws.tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.deserializers.ConversionObjectDeserializer;
import net.hawkengine.core.utilities.deserializers.WsContractDeserializer;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.ws.WsEndpoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

@SuppressWarnings("RedundantArrayCreation")
public class WsEndpointTest {

    private Gson jsonConverter;
    private ConversionObjectDeserializer deserializer;

    //need to do add path to Classpath with reflection since the URLClassLoader.addURL(URL url) method is protected:
    public static void addPath(String s) throws Exception {
        File file = new File(s);
        URI u = file.toURI();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(urlClassLoader, new Object[]{u.toURL()});
    }

    @BeforeClass
    public static void setUpClass() {
        ServerConfiguration.configure();
    }

    @Before
    public void setup() {
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(WsContractDto.class, new WsContractDeserializer())
                .create();
        this.deserializer = new ConversionObjectDeserializer();
    }

    @Test
    public final void resolve_valid_json() {

        //arrange
        WsEndpoint wsep = new WsEndpoint();
        String message = "{\n" +
                "\"className\": \"testClass\",\n" +
                "\"packageName\": \"testPackage\",\n" +
                "\"methodName\": \"testMethod\",\n" +
                "\"result\": \"testResult\",\n" +
                "\"error\": \"testError\",\n" +
                "\"errorMessage\": \"testErrorMessage\",\n" +
                "\"args\": [{\n" +
                "\"packageName\": \"testPackage\",\n" +
                "\"object\": {\"testObject\" : \"someValue\"\n}" +
                "}]\n" +
                "}";
        //act
        WsContractDto contract = wsep.resolve(message);

        //assert
        Assert.assertNotNull(contract);
    }

    @Test
    public void call_existing_service() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

//        //arrange
//        WsEndpoint ep = new WsEndpoint();
//
//        WsContractDto contract = new WsContractDto();
//        contract.setClassName("PipelineService");
//        contract.setPackageName("net.hawkengine.services");
//        contract.setMethodName("add");
//        contract.setResult("");
//        contract.setError(false);
//        contract.setResult("");
//        ConversionObject conversionObject = new ConversionObject();
//
//        try {
//            addPath("/");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        //act
//        Object result = ep.call(contract);
//
//        //assert
//        //Assert.assertNotNull(result);

    }


}
