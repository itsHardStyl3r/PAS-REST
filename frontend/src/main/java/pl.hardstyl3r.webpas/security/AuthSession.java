package pl.hardstyl3r.webpas.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

@Component
@SessionScope
public class AuthSession implements Serializable {

    private String token;
    private String username;
    private String role;

    public boolean isAuthenticated() {
        return token != null && !token.isBlank();
    }

    public void set(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public void clear() {
        this.token = null;
        this.username = null;
        this.role = null;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
