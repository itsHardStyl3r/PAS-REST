package pl.hardstyl3r.pas.v1.dto;

import jakarta.validation.constraints.NotBlank;

public record AllocationRequest(
        @NotBlank(message = "Identyfikator użytkownika nie może być pusty.")
        String userId,

        @NotBlank(message = "Identyfikator zasobu nie może być pusty.")
        String resourceId
) {
}
