package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.bson.Document;
import org.bson.types.ObjectId;
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
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AllocationRESTTest {

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

    private String activeUserId;
    private String inactiveUserId;
    private String availableResourceId;
    private String allocatedResourceId;
    private String currentAllocationId;
    private String pastAllocationId;

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

        // Setup Users
        Document activeUser = new Document("username", "activeUser").append("name", "Active User").append("active", true);
        Document inactiveUser = new Document("username", "inactiveUser").append("name", "Inactive User").append("active", false);
        users.insertMany(Arrays.asList(activeUser, inactiveUser));
        activeUserId = activeUser.getObjectId("_id").toHexString();
        inactiveUserId = inactiveUser.getObjectId("_id").toHexString();

        // Setup Resources
        Document availableResource = new Document("_class", Book.class.getName()).append("name", "Available Book");
        Document allocatedResource = new Document("_class", Book.class.getName()).append("name", "Allocated Book");
        resources.insertMany(Arrays.asList(availableResource, allocatedResource));
        availableResourceId = availableResource.getObjectId("_id").toHexString();
        allocatedResourceId = allocatedResource.getObjectId("_id").toHexString();

        // Setup Allocations
        Document currentAllocation = new Document("userId", activeUserId).append("resourceId", allocatedResourceId).append("startTime", LocalDateTime.now()).append("endTime", null);
        Document pastAllocation = new Document("userId", activeUserId).append("resourceId", new ObjectId().toHexString()).append("startTime", LocalDateTime.now().minusDays(5)).append("endTime", LocalDateTime.now().minusDays(2));
        allocations.insertMany(Arrays.asList(currentAllocation, pastAllocation));
        currentAllocationId = currentAllocation.getObjectId("_id").toHexString();
        pastAllocationId = pastAllocation.getObjectId("_id").toHexString();
    }

    @Test
    void shouldCreateAllocation() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", activeUserId);
        payload.put("resourceId", availableResourceId);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("userId", equalTo(activeUserId))
                .body("resourceId", equalTo(availableResourceId))
                .body("endTime", nullValue());
    }

    @Test
    void shouldEndAllocation() {
        given()
                .pathParam("id", currentAllocationId)
                .when()
                .post("/api/v1/allocations/{id}/end")
                .then()
                .statusCode(200)
                .body("id", equalTo(currentAllocationId))
                .body("endTime", notNullValue());
    }

    @Test
    void shouldGetAllAllocations() {
        given()
                .when()
                .get("/api/v1/allocations")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));
    }

    @Test
    void shouldGetCurrentAllocationsForUser() {
        given()
                .pathParam("userId", activeUserId)
                .when()
                .get("/api/v1/allocations/user/{userId}/current")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(currentAllocationId));
    }

    @Test
    void shouldGetPastAllocationsForUser() {
        given()
                .pathParam("userId", activeUserId)
                .when()
                .get("/api/v1/allocations/user/{userId}/past")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(pastAllocationId));
    }

    @Test
    void shouldFailToCreateAllocationForInactiveUser() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", inactiveUserId);
        payload.put("resourceId", availableResourceId);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldFailToCreateAllocationForAlreadyAllocatedResource() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", activeUserId);
        payload.put("resourceId", allocatedResourceId);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldFailToDeleteEndedAllocation() {
        given()
                .pathParam("id", pastAllocationId)
                .when()
                .delete("/api/v1/allocations/{id}")
                .then()
                .statusCode(400);
    }
}
