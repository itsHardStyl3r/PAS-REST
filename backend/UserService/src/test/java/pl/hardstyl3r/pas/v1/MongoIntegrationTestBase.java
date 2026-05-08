package pl.hardstyl3r.pas.v1;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class MongoIntegrationTestBase {

    static final MongoDBContainer MONGO_DB_CONTAINER =
            new MongoDBContainer(DockerImageName.parse("mongo:7.0"));

    static {
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }
}
