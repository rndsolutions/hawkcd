package net.hawkengine.model.dto;

import net.hawkengine.model.payload.Permission;

import java.util.List;

public class UserDto {
    private String username;
    private List<Permission> permissions;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
