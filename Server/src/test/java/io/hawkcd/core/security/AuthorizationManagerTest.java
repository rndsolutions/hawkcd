package io.hawkcd.core.security;

import org.junit.Test;

import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;

import static org.junit.Assert.*;

/**
 * Created by rado on 14.11.16.
 */
public class AuthorizationManagerTest {

    @Test
    public void isAuthorized_exactmatch() throws Exception {

        //assemble
        WsContractDto contract =  new WsContractDto();
        contract.setClassName("PipelineDefinitionService");
        contract.setPackageName("io.hawkcd.services");
        contract.setMethodName("getAll");

        User user = new User();
        user.setEmail("admin@admin.com");


        //user.setPermissions();

        IAuthorizationManager authorizationManager = AuthorizationFactory.getAuthorizationManager();


        //act
        //authorizationManager.isAuthorized()


        //assert
    }

}