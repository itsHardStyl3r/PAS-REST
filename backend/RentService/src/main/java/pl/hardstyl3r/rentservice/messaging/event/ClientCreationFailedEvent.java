package pl.hardstyl3r.rentservice.messaging.event;

public record ClientCreationFailedEvent(String userId, String reason) {
}
