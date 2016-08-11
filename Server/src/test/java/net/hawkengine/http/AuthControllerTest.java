package net.hawkengine.http;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AuthControllerTest {

    @Test
    public void regextTest(){

        //arrange
        String stringToMatch = "access_token=d2ebf8231cccea4be9e8943b0caab088b6448e67&scope=repo%2Cuser%3Aemail&token_type=bearer ";
        String stringToTest ="access_token=f337fd61c0cd7a77501d5e013d1e8e4b9d147741&scope=repo%2Cuser%3Aemail&token_type=bearer";
        Pattern p = Pattern.compile("access_token=.*?&");

        //act
        boolean b = Pattern.matches("access_token=.*?&",stringToTest);

        //assert
        Matcher m = p.matcher(stringToTest);
        String expected = null;
        if (m.find()){
            expected = m.group(0);
        }

        Assert.assertNotNull(expected);

    }
}
