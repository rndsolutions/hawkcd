package io.hawkcd.http.security;

import com.google.common.reflect.ClassPath;
import io.hawkcd.core.MessageConverter;
import io.hawkcd.core.security.AuthorizationFactory;
import io.hawkcd.core.subscriber.Envelopе;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.WsContractDto;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.print.DocFlavor;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.SecurityContext;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rado on 07.12.16.
 */
public class HttpSecurityContext implements SecurityContext {

    private PrincipalUser principalUser;
    private ResourceInfo resourceInfo;
    private Field servicePackageNameField;
    private Field serviceClassNameField;
    private String methodName;


    public HttpSecurityContext(User user, ResourceInfo resourceInfo) throws NoSuchFieldException {
        this.principalUser = new PrincipalUser(user);
        this.resourceInfo = resourceInfo;
        this.methodName = resourceInfo.getResourceMethod().getName();
        servicePackageNameField = resourceInfo.getResourceClass().getDeclaredField("SERVICE_PACKAGE_NAME");
        serviceClassNameField = resourceInfo.getResourceClass().getDeclaredField("SERVICE_CLASS_NAME");
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principalUser;
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new NotImplementedException();
    }

    @Override
    public boolean isSecure() {
        throw new NotImplementedException();
    }

    @Override
    public String getAuthenticationScheme() {
        throw new NotImplementedException();
    }

    public boolean isAuthorized() throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException {

        String servicePackageName = (String) servicePackageNameField.get(resourceInfo.getResourceClass().getClass());
        String serviceClassName = (String) serviceClassNameField.get(resourceInfo.getResourceClass().getClass());

        WsContractDto contractDto = MessageConverter.convert(serviceClassName, servicePackageName, methodName);

        //TODO: Consider refactor isAuthorized to handle null as List<Object>
        List<Envelopе> list = new ArrayList<>();

        return AuthorizationFactory.getAuthorizationManager().isAuthorized(this.principalUser.getUser(), contractDto, list);
    }
}