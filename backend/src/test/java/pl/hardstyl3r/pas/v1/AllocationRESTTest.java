package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.hardstyl3r.pas.v1.dto.LoginRequest;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.objects.resources.Book;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AllocationRESTTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.users}")
    private String usersCollectionName;
    @Value("${pas.mongodb.collection.resources}")
    private String resourcesCollectionName;
    @Value("${pas.mongodb.collection.allocations}")
    private String allocationsCollectionName;

    private String activeUserId;
    private String availableResourceId;
    private String allocatedResourceId;
    private String currentAllocationId;
    private String adminToken;

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

        Document adminUser = new Document("username", "admin").append("name", "Admin User").append("active", true).append("password", passwordEncoder.encode("password")).append("role", UserRole.ADMIN.name());
        users.insertOne(adminUser);
        activeUserId = adminUser.getObjectId("_id").toHexString();

        adminToken = loginAndGetToken("admin", "password");

        Document availableResource = new Document("_class", Book.class.getName()).append("name", "Available Book");
        Document allocatedResource = new Document("_class", Book.class.getName()).append("name", "Allocated Book");
        resources.insertMany(Arrays.asList(availableResource, allocatedResource));
        availableResourceId = availableResource.getObjectId("_id").toHexString();
        allocatedResourceId = allocatedResource.getObjectId("_id").toHexString();

        Document currentAllocation = new Document("userId", activeUserId).append("resourceId", allocatedResourceId).append("startTime", LocalDateTime.now()).append("endTime", null);
        allocations.insertOne(currentAllocation);
        currentAllocationId = currentAllocation.getObjectId("_id").toHexString();
    }

    private String loginAndGetToken(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        return response.jsonPath().getString("token");
    }

    @Test
    void shouldCreateAllocation() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", activeUserId);
        payload.put("resourceId", availableResourceId);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(200)
                .body("userId", equalTo(activeUserId));
    }

    @Test
    void shouldEndAllocation() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", currentAllocationId)
                .when()
                .post("/api/v1/allocations/{id}/end")
                .then()
                .statusCode(200)
                .body("endTime", notNullValue());
    }

    @Test
    void shouldFailToEndNonExistentAllocation() {
        String nonExistentId = "60c72b2f9b1e8b3b3c8b4567";
        given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", nonExistentId)
                .when()
                .post("/api/v1/allocations/{id}/end")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldFailToCreateAllocationWithoutAuth() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", activeUserId);
        payload.put("resourceId", availableResourceId);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(403);
    }

    @Test
    void shouldFailToCreateAllocationForAllocatedResource() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", activeUserId);
        payload.put("resourceId", allocatedResourceId);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(409);
    }

}
