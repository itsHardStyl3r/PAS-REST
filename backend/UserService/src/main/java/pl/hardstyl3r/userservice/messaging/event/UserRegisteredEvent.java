package pl.hardstyl3r.userservice.messaging.event;

public record UserRegisteredEvent(String userId, String username, String name, String role, boolean active) {
}
