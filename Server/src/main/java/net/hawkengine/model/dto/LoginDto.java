package net.hawkengine.model.dto;

public class LoginDto {
    private String email;

    private String password;

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
}