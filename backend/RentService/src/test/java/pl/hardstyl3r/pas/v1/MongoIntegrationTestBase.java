package pl.hardstyl3r.pas.v1;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class MongoIntegrationTestBase {

    static final MongoDBContainer MONGO_DB_CONTAINER =
            new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    static final RabbitMQContainer RABBIT_MQ_CONTAINER =
            new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"));

    static {
        MONGO_DB_CONTAINER.start();
        RABBIT_MQ_CONTAINER.start();
    }

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
        registry.add("spring.rabbitmq.host", RABBIT_MQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBIT_MQ_CONTAINER::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT_MQ_CONTAINER::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT_MQ_CONTAINER::getAdminPassword);
    }
}
