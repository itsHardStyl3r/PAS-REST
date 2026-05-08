package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.hardstyl3r.rentservice.dto.AllocationRequest;
import pl.hardstyl3r.rentservice.domain.resource.Book;
import pl.hardstyl3r.rentservice.security.JwtUtil;
import pl.hardstyl3r.rentservice.RentServiceApplication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@SpringBootTest(classes = RentServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AllocationRESTTest extends MongoIntegrationTestBase {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.clients}")
    private String clientsCollectionName;
    @Value("${pas.mongodb.collection.resources}")
    private String resourcesCollectionName;
    @Value("${pas.mongodb.collection.allocations}")
    private String allocationsCollectionName;

    private String activeUserId;
    private String inactiveUserId;
    private String availableResourceId;
    private String allocatedResourceId;
    private String currentAllocationId;
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

        Document adminUser = new Document("username", "admin").append("name", "Admin User").append("active", true).append("password", passwordEncoder.encode("password")).append("role", "ADMIN");
        users.insertOne(adminUser);
        activeUserId = adminUser.getObjectId("_id").toHexString();

        Document inactiveUser = new Document("username", "user").append("name", "Inactive User").append("active", false).append("password", passwordEncoder.encode("password")).append("role", "CLIENT");
        users.insertOne(inactiveUser);
        inactiveUserId = inactiveUser.getObjectId("_id").toHexString();

        adminToken = jwtUtil.generateToken(new User("admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        Document availableResource = new Document("_class", "pl.hardstyl3r.rentservice.adapters.resource.BookEnt").append("name", "Available Book");
        Document allocatedResource = new Document("_class", "pl.hardstyl3r.rentservice.adapters.resource.BookEnt").append("name", "Allocated Book");
        resources.insertMany(Arrays.asList(availableResource, allocatedResource));
        availableResourceId = availableResource.getObjectId("_id").toHexString();
        allocatedResourceId = allocatedResource.getObjectId("_id").toHexString();

        Document currentAllocation = new Document("userId", activeUserId).append("resourceId", allocatedResourceId).append("startTime", LocalDateTime.now()).append("endTime", null);
        allocations.insertOne(currentAllocation);
        currentAllocationId = currentAllocation.getObjectId("_id").toHexString();
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

    @Test
    void shouldFailToCreateAllocationForInactiveUser() {
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", inactiveUserId);
        payload.put("resourceId", availableResourceId);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldFailToCreateAllocationForNonExistentUser() {
        String nonExistentUserId = "60c72b2f9b1e8b3b3c8b4567";
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", nonExistentUserId);
        payload.put("resourceId", availableResourceId);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldFailToCreateAllocationForNonExistentResource() {
        String nonExistentResourceId = "60c72b2f9b1e8b3b3c8b4568";
        Map<String, String> payload = new HashMap<>();
        payload.put("userId", activeUserId);
        payload.put("resourceId", nonExistentResourceId);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAllocationRequests")
    void shouldFailToCreateAllocationWithInvalidData(AllocationRequest request) {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/allocations")
                .then()
                .statusCode(400);
    }

    private static Stream<Arguments> provideInvalidAllocationRequests() {
        return Stream.of(
                Arguments.of(new AllocationRequest(null, "resourceId")),
                Arguments.of(new AllocationRequest("", "resourceId")),
                Arguments.of(new AllocationRequest("   ", "resourceId")),
                Arguments.of(new AllocationRequest("userId", null)),
                Arguments.of(new AllocationRequest("userId", "")),
                Arguments.of(new AllocationRequest("userId", "   "))
        );
    }
}
