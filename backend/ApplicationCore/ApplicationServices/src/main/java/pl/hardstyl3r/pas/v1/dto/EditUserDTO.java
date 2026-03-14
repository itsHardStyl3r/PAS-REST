package pl.hardstyl3r.pas.v1.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditUserDTO(
        @NotBlank(message = "Imię nie może być puste.")
        @Size(min = 3, max = 64, message = "Imię musi mieć od 3 do 64 znaków.")
        String name
) {
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
