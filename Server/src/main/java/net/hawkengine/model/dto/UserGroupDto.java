package net.hawkengine.model.dto;

import net.hawkengine.model.User;
import net.hawkengine.model.payload.Permission;

import java.util.ArrayList;
import java.util.List;

public class UserGroupDto {
    private String id;
    private String name;
    private List<String> userIds;
    private List<User> users;
    private List<Permission> permissions;

    public UserGroupDto() {
        this.setUsers(new ArrayList<>());
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
