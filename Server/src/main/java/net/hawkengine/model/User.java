package net.hawkengine.model;

public class User extends DbEntry {

    private String email;
    private String password;
    private String ghAuthCode;
    private String provider;
//    private String token;

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email =  email;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getGhAuthCode() {
        return ghAuthCode;
    }

    public void setGhAuthCode(String ghAuthCode) {
        this.ghAuthCode = ghAuthCode;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

//    public String getToken() {
//        return this.token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
}
