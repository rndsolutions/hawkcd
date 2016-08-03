package net.hawkengine.model.dto;

import net.hawkengine.model.payload.Permission;

import java.util.List;

public class RegisterDto {
    private String email;
    private String password;
    private String confirmPassword;
    private List<Permission> permissions;

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}