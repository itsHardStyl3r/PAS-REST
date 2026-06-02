package pl.hardstyl3r.userservice.config;

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

    public static final String RK_USER_REGISTERED = "user.registered";
    public static final String RK_CLIENT_CREATED = "client.created";
    public static final String RK_CLIENT_FAILED = "client.creation-failed";

    public static final String QUEUE_CLIENT_CREATED = "user.client-created.q";
    public static final String QUEUE_CLIENT_FAILED = "user.client-failed.q";

    @Bean
    public TopicExchange pasExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue clientCreatedQueue() {
        return QueueBuilder.durable(QUEUE_CLIENT_CREATED).build();
    }

    @Bean
    public Queue clientFailedQueue() {
        return QueueBuilder.durable(QUEUE_CLIENT_FAILED).build();
    }

    @Bean
    public Binding clientCreatedBinding() {
        return BindingBuilder.bind(clientCreatedQueue()).to(pasExchange()).with(RK_CLIENT_CREATED);
    }

    @Bean
    public Binding clientFailedBinding() {
        return BindingBuilder.bind(clientFailedQueue()).to(pasExchange()).with(RK_CLIENT_FAILED);
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
