package net.hawkengine.model.dto;

/**
 * Created by rado on 17.07.16.
 */
public class RegisterDto {


    private String email;

    private String password;

    private String confirmPassword;

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
}
