package net.hawkengine.model;

import java.util.ArrayList;
import java.util.List;

public class UserGroup extends DbEntry {
    private String name;
    private List<String> userIds;
    private List<Permission> permissions;

    public UserGroup(){
        this.setUserIds(new ArrayList<>());
        this.setPermissions(new ArrayList<>());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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