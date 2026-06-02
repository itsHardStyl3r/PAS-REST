package pl.hardstyl3r.rentservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pas.exchange";
    public static final String DLX = "pas.dlx";

    public static final String RK_USER_REGISTERED = "user.registered";
    public static final String RK_CLIENT_CREATED = "client.created";
    public static final String RK_CLIENT_FAILED = "client.creation-failed";

    public static final String QUEUE_USER_REGISTERED = "rent.user-registered.q";
    public static final String QUEUE_USER_REGISTERED_DLQ = "rent.user-registered.dlq";
    public static final String RK_USER_REGISTERED_DLQ = "rent.user-registered.dlq";

    @Bean
    public TopicExchange pasExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLX, true, false);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable(QUEUE_USER_REGISTERED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", RK_USER_REGISTERED_DLQ)
                .build();
    }

    @Bean
    public Queue userRegisteredDlq() {
        return QueueBuilder.durable(QUEUE_USER_REGISTERED_DLQ).build();
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder.bind(userRegisteredQueue()).to(pasExchange()).with(RK_USER_REGISTERED);
    }

    @Bean
    public Binding userRegisteredDlqBinding() {
        return BindingBuilder.bind(userRegisteredDlq()).to(deadLetterExchange()).with(RK_USER_REGISTERED_DLQ);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
