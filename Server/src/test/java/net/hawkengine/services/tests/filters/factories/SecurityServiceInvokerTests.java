package net.hawkengine.services.tests.filters.factories;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.PipelineAuthorizationService;
import net.hawkengine.services.filters.SecurityService;
import net.hawkengine.services.filters.factories.AuthorizationServiceFactory;
import net.hawkengine.services.filters.factories.SecurityServiceInvoker;
import net.hawkengine.services.filters.interfaces.ISecurityService;
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
    }

    @Test
    public void passedMethodName_getAll_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("Get All Service Result");

        Mockito.when(this.mockedSecurityService.getAll(this.contract, this.permissions)).thenReturn(expectedServiceResult);

        //Act
        this.contract.setMethodName("getAll");
        ServiceResult actualServiceResult = this.securityServiceInvoker.process(contract, permissions);

        //Assert
        Assert.assertEquals(expectedServiceResult.getMessage(), actualServiceResult.getMessage());
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void passedMethodName_getById_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("GET BY ID Service Result");

        Mockito.when(this.mockedSecurityService.getById(this.contract, this.permissions)).thenReturn(expectedServiceResult);

        //Act
        this.contract.setMethodName("getById");
        ServiceResult actualServiceResult = this.securityServiceInvoker.process(contract, permissions);

        //Assert
        Assert.assertEquals(expectedServiceResult.getMessage(), actualServiceResult.getMessage());
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void passedMethodName_add_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("ADD Service Result");

        Mockito.when(this.mockedSecurityService.add(this.contract, this.permissions)).thenReturn(expectedServiceResult);

        //Act
        this.contract.setMethodName("add");
        ServiceResult actualServiceResult = this.securityServiceInvoker.process(contract, permissions);

        //Assert
        Assert.assertEquals(expectedServiceResult.getMessage(), actualServiceResult.getMessage());
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void passedMethodName_update_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("Update Service Result");

        Mockito.when(this.mockedSecurityService.update(this.contract, this.permissions)).thenReturn(expectedServiceResult);

        //Act
        this.contract.setMethodName("update");
        ServiceResult actualServiceResult = this.securityServiceInvoker.process(contract, permissions);

        //Assert
        Assert.assertEquals(expectedServiceResult.getMessage(), actualServiceResult.getMessage());
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void passedMethodName_delete_correctServiceResult() throws Exception {
        //Assert
        ServiceResult expectedServiceResult = new ServiceResult();
        expectedServiceResult.setError(false);
        expectedServiceResult.setObject(null);
        expectedServiceResult.setMessage("DELETE Service Result");

        Mockito.when(this.mockedSecurityService.delete(this.contract, this.permissions)).thenReturn(expectedServiceResult);

        //Act
        this.contract.setMethodName("delete");
        ServiceResult actualServiceResult = this.securityServiceInvoker.process(contract, permissions);

        //Assert
        Assert.assertEquals(expectedServiceResult.getMessage(), actualServiceResult.getMessage());
        Assert.assertFalse(actualServiceResult.hasError());
    }

    @Test
    public void passedMethodName_invalidMethodName_null() throws Exception {

        //Act
        this.contract.setMethodName("invalidName");
        ServiceResult actualServiceResult = this.securityServiceInvoker.process(contract, permissions);

        //Assert
        Assert.assertNull(actualServiceResult);
    }

    @Test
    public void securityServiceInvoker_ctreateInstance_notNull() {
        this.securityServiceInvoker = new SecurityServiceInvoker();

        Assert.assertNotNull(this.securityServiceInvoker);
    }
}
