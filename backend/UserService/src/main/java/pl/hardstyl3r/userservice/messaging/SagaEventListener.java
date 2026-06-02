package pl.hardstyl3r.userservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.hardstyl3r.userservice.config.RabbitConfig;
import pl.hardstyl3r.userservice.messaging.event.ClientCreatedEvent;
import pl.hardstyl3r.userservice.messaging.event.ClientCreationFailedEvent;
import pl.hardstyl3r.userservice.ports.driving.UserViewPort;

@Component
public class SagaEventListener {

    private final UserViewPort userViewPort;

    public SagaEventListener(UserViewPort userViewPort) {
        this.userViewPort = userViewPort;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_CLIENT_CREATED)
    public void onClientCreated(ClientCreatedEvent event) {
        userViewPort.userActivationById(event.userId(), true);
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_CLIENT_FAILED)
    public void onClientCreationFailed(ClientCreationFailedEvent event) {
        userViewPort.deleteUserById(event.userId());
    }
}
