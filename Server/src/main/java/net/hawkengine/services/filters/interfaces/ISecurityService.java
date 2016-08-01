package net.hawkengine.services.filters.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.dto.WsContractDto;
import net.hawkengine.model.payload.Permission;

import java.util.List;

public interface ISecurityService {
    ServiceResult getAll(WsContractDto contract, List<Permission> permissions);

    ServiceResult getPipelineDTOs(WsContractDto contract, List<Permission> permissions);

    ServiceResult getById(WsContractDto contract, List<Permission> permissions);

    ServiceResult add(WsContractDto contract, List<Permission> permissions);

    ServiceResult update(WsContractDto contract, List<Permission> permissions);

    ServiceResult delete(WsContractDto contract, List<Permission> permissions);

    ServiceResult addUserToGroup(WsContractDto contract, List<Permission> permissions);

    ServiceResult removeUserFromGroup(WsContractDto contract, List<Permission> permissions);

    ServiceResult getAllUserGroups(WsContractDto contract, List<Permission> permissions);
}
