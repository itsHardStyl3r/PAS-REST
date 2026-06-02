package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.hardstyl3r.rentservice.RentServiceApplication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@SpringBootTest(classes = RentServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResourceAllocationRESTTest extends MongoIntegrationTestBase {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.clients}")
    private String clientsCollectionName;
    @Value("${pas.mongodb.collection.resources}")
    private String resourcesCollectionName;
    @Value("${pas.mongodb.collection.allocations}")
    private String allocationsCollectionName;

    private String userId;
    private String allocatedResourceId;
    private String unallocatedResourceId;
    private String adminToken;

    @BeforeEach
    void setup() {
        RestAssured.port = port;

        mongoTemplate.dropCollection(clientsCollectionName);
        mongoTemplate.dropCollection(resourcesCollectionName);
        mongoTemplate.dropCollection(allocationsCollectionName);

        MongoCollection<Document> users = mongoTemplate.createCollection(clientsCollectionName);
        MongoCollection<Document> resources = mongoTemplate.createCollection(resourcesCollectionName);
        MongoCollection<Document> allocations = mongoTemplate.createCollection(allocationsCollectionName);

        Document user = new Document("username", "adminUser").append("name", "Admin User").append("active", true).append("password", passwordEncoder.encode("password")).append("role", "ADMIN");
        users.insertOne(user);
        userId = user.getObjectId("_id").toHexString();
        adminToken = TestTokenFactory.generateToken(new User("adminUser", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        Document allocatedResource = new Document("_class", "pl.hardstyl3r.rentservice.adapters.resource.BookEnt").append("name", "Allocated Book");
        Document unallocatedResource = new Document("_class", "pl.hardstyl3r.rentservice.adapters.resource.BookEnt").append("name", "Unallocated Book");
        resources.insertMany(Arrays.asList(allocatedResource, unallocatedResource));
        allocatedResourceId = allocatedResource.getObjectId("_id").toHexString();
        unallocatedResourceId = unallocatedResource.getObjectId("_id").toHexString();

        Document allocation = new Document("userId", userId).append("resourceId", allocatedResourceId).append("startTime", LocalDateTime.now()).append("endTime", null);
        allocations.insertOne(allocation);
    }

    @Test
    void shouldFailToDeleteResourceWhenAllocated() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", allocatedResourceId)
                .when()
                .delete("/api/v1/resources/{id}")
                .then()
                .statusCode(409);
    }

    @Test
    void shouldDeleteResourceWhenNotAllocated() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", unallocatedResourceId)
                .when()
                .delete("/api/v1/resources/{id}")
                .then()
                .statusCode(204);
    }
}
