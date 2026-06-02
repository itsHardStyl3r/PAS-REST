package pl.hardstyl3r.rentservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.hardstyl3r.rentservice.config.RabbitConfig;
import pl.hardstyl3r.rentservice.domain.Client;
import pl.hardstyl3r.rentservice.messaging.event.ClientCreatedEvent;
import pl.hardstyl3r.rentservice.messaging.event.ClientCreationFailedEvent;
import pl.hardstyl3r.rentservice.messaging.event.UserRegisteredEvent;
import pl.hardstyl3r.rentservice.ports.driven.ClientPort;

@Component
public class UserRegisteredListener {

    private final ClientPort clientPort;
    private final RentEventPublisher eventPublisher;

    public UserRegisteredListener(ClientPort clientPort, RentEventPublisher eventPublisher) {
        this.clientPort = clientPort;
        this.eventPublisher = eventPublisher;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_USER_REGISTERED)
    public void onUserRegistered(UserRegisteredEvent event) {
        Client client = new Client(event.userId(), event.username(), true);
        clientPort.save(client);
        eventPublisher.publishClientCreated(new ClientCreatedEvent(event.userId()));
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_USER_REGISTERED_DLQ)
    public void onUserRegisteredFailed(UserRegisteredEvent event) {
        eventPublisher.publishClientCreationFailed(
                new ClientCreationFailedEvent(event.userId(), "Utworzenie klienta nie powiodlo sie po wyczerpaniu prob"));
    }
}
