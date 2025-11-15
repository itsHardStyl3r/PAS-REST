package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import io.restassured.RestAssured;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import pl.hardstyl3r.pas.v1.objects.resources.Book;

import java.time.LocalDateTime;
import java.util.Arrays;

import static io.restassured.RestAssured.given;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResourceAllocationRESTTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.users}")
    private String usersCollectionName;
    @Value("${pas.mongodb.collection.resources}")
    private String resourcesCollectionName;
    @Value("${pas.mongodb.collection.allocations}")
    private String allocationsCollectionName;

    private String userId;
    private String allocatedResourceId;
    private String unallocatedResourceId;

    @BeforeAll
    void checkDatabaseConnection() {
        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
        } catch (Exception e) {
            Assumptions.abort("Could not connect to MongoDB. Skipping integration tests.");
        }
    }

    @BeforeEach
    void setup() {
        RestAssured.port = port;

        mongoTemplate.dropCollection(usersCollectionName);
        mongoTemplate.dropCollection(resourcesCollectionName);
        mongoTemplate.dropCollection(allocationsCollectionName);

        MongoCollection<Document> users = mongoTemplate.createCollection(usersCollectionName);
        MongoCollection<Document> resources = mongoTemplate.createCollection(resourcesCollectionName);
        MongoCollection<Document> allocations = mongoTemplate.createCollection(allocationsCollectionName);

        // Setup User
        Document user = new Document("username", "testUser").append("name", "Test User").append("active", true);
        users.insertOne(user);
        userId = user.getObjectId("_id").toHexString();

        // Setup Resources
        Document allocatedResource = new Document("_class", Book.class.getName()).append("name", "Allocated Book");
        Document unallocatedResource = new Document("_class", Book.class.getName()).append("name", "Unallocated Book");
        resources.insertMany(Arrays.asList(allocatedResource, unallocatedResource));
        allocatedResourceId = allocatedResource.getObjectId("_id").toHexString();
        unallocatedResourceId = unallocatedResource.getObjectId("_id").toHexString();

        // Setup Allocation
        Document allocation = new Document("userId", userId)
                .append("resourceId", allocatedResourceId)
                .append("startTime", LocalDateTime.now())
                .append("endTime", null);
        allocations.insertOne(allocation);
    }

    @Test
    void shouldFailToDeleteResourceWhenAllocated() {
        given()
                .pathParam("id", allocatedResourceId)
                .when()
                .delete("/api/v1/resources/{id}")
                .then()
                .statusCode(409);
    }

    @Test
    void shouldDeleteResourceWhenNotAllocated() {
        given()
                .pathParam("id", unallocatedResourceId)
                .when()
                .delete("/api/v1/resources/{id}")
                .then()
                .statusCode(204);
    }
}
