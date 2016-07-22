package net.hawkengine.services.filters;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;
import net.hawkengine.services.filters.interfaces.IAuthorizationService;
import net.hawkengine.services.filters.interfaces.ISecurityService;
import net.hawkengine.ws.WsEndpoint;
import net.hawkengine.ws.WsObjectProcessor;
import org.apache.log4j.Logger;

import java.util.List;

public class SecurityService implements ISecurityService {
    private static final Logger LOGGER = Logger.getLogger(WsEndpoint.class.getClass());
    private WsObjectProcessor wsObjectProcessor;
    private ServiceResult result;
    private IAuthorizationService authorizationService;

    public SecurityService() {
        this.wsObjectProcessor = new WsObjectProcessor();
        this.result = new ServiceResult();
        this.authorizationService = new AuthorizationService();
    }


    @Override
    public ServiceResult getAll(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
//            List<?> entitiesToFilter = (List<?>) this.result.getObject();
//            String entityType = contract.getClassName().substring(0, contract.getClassName().length() - 7);
//            List<?> filteredEntities = this.authorizationService.getAll(permissions, entitiesToFilter);
//            this.result.setObject(filteredEntities);
//            int a =5;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult getPipelineDTOs(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
            List<?> entitiesToFilter = (List<?>) this.result.getObject();
            String entityType = contract.getClassName().substring(0, contract.getClassName().length() - 7);
            List<?> filteredEntities = this.authorizationService.getAll(permissions, entitiesToFilter);
            this.result.setObject(filteredEntities);
            int a =5;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult getById(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult add(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult update(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }

    @Override
    public ServiceResult delete(WsContractDto contract, List<Permission> permissions) {
        try {
            this.result = (ServiceResult) this.wsObjectProcessor.call(contract);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return this.result;
    }
}
