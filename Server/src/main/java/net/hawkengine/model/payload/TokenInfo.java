package net.hawkengine.model.payload;

import net.hawkengine.model.User;

import java.time.LocalDateTime;

public class TokenInfo {
    private User user;
    private LocalDateTime issued;
    private LocalDateTime expires;


    public TokenInfo() {
        this.issued = LocalDateTime.now();
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getIssued() {
        return this.issued;
    }

    public LocalDateTime getExpires() {
        return this.expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }
}