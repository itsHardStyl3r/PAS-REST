package pl.hardstyl3r.pas.v1.dto;

import pl.hardstyl3r.pas.v1.objects.UserRole;

public record UserDTO(String id, String username, String name, boolean active, UserRole role) {
    public UserDTO(String id, String username, String name, boolean active, UserRole role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.active = active;
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", role=" + role +
                '}';
    }

    public String username() {
        return username;
    }

    public boolean active() {
        return active;
    }

    public UserRole role() {
        return role;
    }
}
