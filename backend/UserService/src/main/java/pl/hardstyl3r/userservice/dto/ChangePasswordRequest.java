package pl.hardstyl3r.userservice.dto;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
