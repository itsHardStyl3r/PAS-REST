package pl.hardstyl3r.rentservice.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.hardstyl3r.rentservice.config.RabbitConfig;
import pl.hardstyl3r.rentservice.messaging.event.ClientCreatedEvent;
import pl.hardstyl3r.rentservice.messaging.event.ClientCreationFailedEvent;

@Component
public class RentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishClientCreated(ClientCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_CLIENT_CREATED, event);
    }

    public void publishClientCreationFailed(ClientCreationFailedEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.RK_CLIENT_FAILED, event);
    }
}
