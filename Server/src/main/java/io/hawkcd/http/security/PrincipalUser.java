package io.hawkcd.http.security;


import io.hawkcd.model.User;

import java.security.Principal;

public class PrincipalUser implements Principal {

    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PrincipalUser(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return this.user.getName();
    }
}