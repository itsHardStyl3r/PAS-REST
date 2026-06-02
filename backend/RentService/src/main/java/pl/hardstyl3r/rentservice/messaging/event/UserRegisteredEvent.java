package pl.hardstyl3r.rentservice.messaging.event;

public record UserRegisteredEvent(String userId, String username, String name, String role, boolean active) {
}
