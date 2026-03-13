package pl.hardstyl3r.pas.v1.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Nazwa użytkownika nie może być pusta.")
        String username,
        @NotBlank(message = "Hasło nie może być puste.")
        String password
) {
}
