package pl.hardstyl3r.pas.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nazwa użytkownika nie może być pusta.")
        @Size(min = 3, max = 32, message = "Nazwa użytkownika musi mieć od 3 do 32 znaków.")
        String username,

        @NotBlank(message = "Hasło nie może być puste.")
        @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków.")
        String password,

        @NotBlank(message = "Imię nie może być puste.")
        @Size(min = 3, max = 64, message = "Imię musi mieć od 3 do 64 znaków.")
        String name
) {
}
