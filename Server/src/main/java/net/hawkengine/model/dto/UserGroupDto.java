package net.hawkengine.model.dto;

import net.hawkengine.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserGroupDto {
    private String id;
    private String name;
    private List<User> users;

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

    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
