package pl.hardstyl3r.pas.v1.dto;

public record UserDTO(String id, String username, String name, boolean active) {
    public UserDTO(String id, String username, String name, boolean active) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.active = active;
    }

    @Override
    public String toString() {
        return "UserDTO{id=" + id + ", username='" + username + "', name='" + name + "', active=" + active + "}";
    }

    public String id() {
        return id;
    }

    public String username() {
        return username;
    }

    public boolean active() {
        return active;
    }
}
