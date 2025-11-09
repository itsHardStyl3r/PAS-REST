package pl.hardstyl3r.pas.v1.dto;

import com.fasterxml.jackson.annotation.JsonGetter;

public record EditUserDTO(String name) {
    public EditUserDTO(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EditUserDTO{name='" + name + "'}";
    }

    @JsonGetter("name")
    public String name() {
        return name;
    }
}
