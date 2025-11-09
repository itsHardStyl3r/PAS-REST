package pl.hardstyl3r.pas.v1.dto;

import com.fasterxml.jackson.annotation.JsonGetter;

public record CreateUserDTO(String username, String name, boolean active) {
    public CreateUserDTO(String username, String name, boolean active) {
        this.username = username;
        this.name = name;
        this.active = active;
    }

    @Override
    public String toString() {
        return "CreateUserDTO{username='" + username + "', name='" + name + "', active=" + active + "}";
    }

    @JsonGetter("name")
    public String name() {
        return name;
    }

    @JsonGetter("username")
    public String username() {
        return username;
    }

    @JsonGetter("active")
    public boolean active() {
        return active;
    }
}
