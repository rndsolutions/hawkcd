package net.hawkengine.model.dto;

import net.hawkengine.model.PermissionObject;
import net.hawkengine.model.User;
import net.hawkengine.model.payload.Permission;

import java.util.ArrayList;
import java.util.List;

public class UserGroupDto extends PermissionObject {
    private String id;
    private String name;
    private List<User> users;
    private List<String> userIds;
    private List<Permission> permissions;

    public UserGroupDto() {
        this.setUsers(new ArrayList<>());
    }

    public String getId() {
        return id;
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

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<String> getUserIds() {
        return this.userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
