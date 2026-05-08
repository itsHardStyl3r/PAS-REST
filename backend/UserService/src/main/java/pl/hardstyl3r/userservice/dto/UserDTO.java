package pl.hardstyl3r.userservice.dto;

import pl.hardstyl3r.pas.v1.objects.UserRole;

public record UserDTO(String id, String username, String name, boolean active, UserRole role) {
}
