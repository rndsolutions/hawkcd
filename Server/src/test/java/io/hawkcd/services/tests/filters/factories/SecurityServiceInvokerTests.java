package io.hawkcd.services.tests.filters.factories;

import io.hawkcd.Config;
import io.hawkcd.utilities.constants.TestsConstants;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.dto.WsContractDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.payload.Permission;
import io.hawkcd.services.filters.PipelineAuthorizationService;
import io.hawkcd.services.filters.SecurityService;
import io.hawkcd.services.filters.factories.AuthorizationServiceFactory;
import io.hawkcd.services.filters.factories.SecurityServiceInvoker;
import io.hawkcd.services.filters.interfaces.ISecurityService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class SecurityServiceInvokerTests {
    private WsContractDto contract;
    private List<Permission> permissions;
    private SecurityServiceInvoker securityServiceInvoker;

    private ISecurityService mockedSecurityService;
    private AuthorizationServiceFactory mockedAuthorizationServiceFactory;
    private PipelineAuthorizationService mockedAuthorizationService;

    @Before
    public void setUp() {
        this.contract = new WsContractDto();
        this.permissions = new ArrayList<>();

        this.mockedSecurityService = Mockito.mock(SecurityService.class);
        this.mockedAuthorizationServiceFactory = Mockito.mock(AuthorizationServiceFactory.class);
        this.mockedAuthorizationService = Mockito.mock(PipelineAuthorizationService.class);

        this.securityServiceInvoker = new SecurityServiceInvoker(this.mockedSecurityService);

        Config.configure();
    }

    @Test
    public void passedMethodName_getAll_correctServiceResult() throws Exception {
        //Assert
        List expectedServiceResult = new ArrayList<>();

        Mockito.when(this.mockedSecurityService.getAll(new ArrayList<>(), this.contract.getClassName(), this.permissions)).thenReturn(expectedServiceResult);

        //Act
        this.contract.setMethodName("getAll");
        List actualServiceResult = this.securityServiceInvoker.filterEntities(expectedServiceResult, contract.getClassName(), permissions, "getAll");

        //Assert
        Assert.assertEquals(TestsConstants.TESTS_COLLECTION_SIZE_NO_OBJECTS, actualServiceResult.size());
    }

    @Test
    public void passedMethodName_getById_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setNotificationType(NotificationType.SUCCESS);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("GET BY ID Service Result");

        Mockito.when(this.mockedSecurityService.getById("entity", this.contract.getClassName(), this.permissions)).thenReturn(true);

        //Act
        this.contract.setMethodName("getById");
        boolean actualServiceResult = this.securityServiceInvoker.process("entity", this.contract.getClassName(), permissions, "getById");

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void passedMethodName_add_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setNotificationType(NotificationType.SUCCESS);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("ADD Service Result");

        Mockito.when(this.mockedSecurityService.add("entity", this.contract.getClassName(), this.permissions)).thenReturn(true);

        //Act
        this.contract.setMethodName("add");
        boolean actualServiceResult = this.securityServiceInvoker.process("entity", contract.getClassName(), permissions, "add");

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void passedMethodName_update_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setNotificationType(NotificationType.SUCCESS);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("Update Service Result");

        Mockito.when(this.mockedSecurityService.update("entity", this.contract.getClassName(), this.permissions)).thenReturn(true);

        //Act
        this.contract.setMethodName("update");
        boolean actualServiceResult = this.securityServiceInvoker.process("entity", contract.getClassName(), permissions, "update");

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void passedMethodName_delete_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setNotificationType(NotificationType.SUCCESS);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("DELETE Service Result");

        Mockito.when(this.mockedSecurityService.delete("entityId", this.contract.getClassName(), this.permissions)).thenReturn(true);

        //Act
        this.contract.setMethodName("delete");
        boolean actualServiceResult = this.securityServiceInvoker.process("entityId", contract.getClassName(), permissions, "delete");

        //Assert
        Assert.assertTrue(actualServiceResult);
    }

    @Test
    public void passedMethodName_invalidMethodName_null() throws Exception {

        //Act
        this.contract.setMethodName("invalidName");
        boolean actualServiceResult = this.securityServiceInvoker.process("entity", contract.getClassName(), permissions, "invalidName");

        //Assert
        Assert.assertFalse(actualServiceResult);
    }

    @Test
    public void securityServiceInvoker_ctreateInstance_notNull() {
        this.securityServiceInvoker = new SecurityServiceInvoker();

        Assert.assertNotNull(this.securityServiceInvoker);
    }
}
