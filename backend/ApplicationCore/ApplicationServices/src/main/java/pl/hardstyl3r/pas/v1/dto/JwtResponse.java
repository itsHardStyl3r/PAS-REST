package pl.hardstyl3r.pas.v1.services.dto;

public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String id;
    private String username;
    private String role;

    public JwtResponse(String token,String refreshToken, String id, String username, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {return refreshToken;}

    public void setRefreshToken(String refreshToken) {this.refreshToken = refreshToken;}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
