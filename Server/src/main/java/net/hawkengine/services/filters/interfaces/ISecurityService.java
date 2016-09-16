package net.hawkengine.services.filters.interfaces;

import net.hawkengine.model.DbEntry;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.payload.Permission;

import java.util.List;

public interface ISecurityService<T extends DbEntry> {
    List<T> getAll(List<T> entitiesToFilter, String className, List<Permission> permissions);

    List<PipelineGroup> getPipelineDTOs(List<T> entitiesToFilter, String className, List<Permission> permissions);

    boolean getById(String entity, String className, List<Permission> permissions);

    boolean add(String entity, String className, List<Permission> permissions);

    boolean update(String entity, String className, List<Permission> permissions);

    boolean delete(String entity, String className, List<Permission> permissions);

    boolean addUserGroupDto(String entity, String className, List<Permission> permissions);

    boolean updateUserGroupDto(String entity, String className, List<Permission> permissions);

    boolean assignUserToGroup(String entity, String className, List<Permission> permissions);

    boolean unassignUserFromGroup(String entity, String className, List<Permission> permissions);

    List<T> getAllUserGroups(List<T> entitiesToFilter, String className, List<Permission> permissions);

    boolean assignPipelineToGroup(String pipelineGroup, String className, List<Permission> permissions);

    boolean unassignPipelineFromGroup(String pipelineGroup, String className, List<Permission> permissions);

    boolean addUserWithoutProvider(String entity, String className, List<Permission> permissions);

    boolean changeUserPassword(String loggedUserEmail, String entity, String className, List<Permission> permissions);

    boolean addWithMaterialDefinition(String entity, String className, List<Permission> permissions);

    boolean addStageWithSpecificJobs(String entity, String className, List<Permission> permissions);
}
