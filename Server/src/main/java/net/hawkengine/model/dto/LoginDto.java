package net.hawkengine.model.dto;

/**
 * Created by rado on 17.07.16.
 */
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
