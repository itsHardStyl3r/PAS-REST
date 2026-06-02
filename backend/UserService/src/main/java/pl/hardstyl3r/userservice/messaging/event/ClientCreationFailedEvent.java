package pl.hardstyl3r.userservice.messaging.event;

public record ClientCreationFailedEvent(String userId, String reason) {
}
