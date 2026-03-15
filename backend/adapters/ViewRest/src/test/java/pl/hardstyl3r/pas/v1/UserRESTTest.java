package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import pl.hardstyl3r.pas.v1.dto.EditUserDTO;
import pl.hardstyl3r.pas.v1.dto.LoginRequest;
import pl.hardstyl3r.pas.v1.dto.RegisterRequest;
import pl.hardstyl3r.pas.v1.dto.UserDTO;
import pl.hardstyl3r.pas.v1.objects.UserRole;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRESTTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.users}")
    private String collectionName;

    private String aniaId;
    private String marekId;
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

        mongoTemplate.dropCollection(collectionName);
        MongoCollection<Document> usersCollection = mongoTemplate.createCollection(collectionName);

        Document admin = new Document("username", "admin").append("name", "Admin").append("active", true).append("password", passwordEncoder.encode("password")).append("role", UserRole.ADMIN.name());
        Document ania = new Document("username", "anna").append("name", "Ania").append("active", true).append("password", passwordEncoder.encode("password")).append("role", UserRole.CLIENT.name());
        Document marek = new Document("username", "marek").append("name", "Marek").append("active", true).append("password", passwordEncoder.encode("password")).append("role", UserRole.CLIENT.name());
        usersCollection.insertMany(Arrays.asList(admin, ania, marek));

        this.aniaId = Objects.requireNonNull(usersCollection.find(Filters.eq("username", "anna")).first()).getObjectId("_id").toHexString();
        this.marekId = Objects.requireNonNull(usersCollection.find(Filters.eq("username", "marek")).first()).getObjectId("_id").toHexString();

        adminToken = loginAndGetToken("admin", "password");
    }

    private String loginAndGetToken(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract().response();
        return response.jsonPath().getString("token");
    }

    @Test
    void shouldRegisterUser() {
        RegisterRequest newUser = new RegisterRequest("kasia", "password123", "Katarzyna");
        given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(200);

        UserDTO createdUser = given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("username", "kasia")
                .when()
                .get("/api/v1/user/username/{username}")
                .then()
                .statusCode(200)
                .extract().as(UserDTO.class);

        assertThat(createdUser.username()).isEqualTo("kasia");
        assertThat(createdUser.name()).isEqualTo("Katarzyna");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRegisterRequests")
    void shouldFailToRegisterUserWithInvalidData(RegisterRequest request) {
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(400);
    }

    private static Stream<Arguments> provideInvalidRegisterRequests() {
        return Stream.of(
                Arguments.of(new RegisterRequest("", "password123", "Test User")),
                Arguments.of(new RegisterRequest("a", "password123", "Test User")),
                Arguments.of(new RegisterRequest("testuser", "", "Test User")),
                Arguments.of(new RegisterRequest("testuser", "123", "Test User")),
                Arguments.of(new RegisterRequest("testuser", "password123", "")),
                Arguments.of(new RegisterRequest("testuser", "password123", "a"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidLoginRequests")
    void shouldFailToLoginWithInvalidData(LoginRequest request) {
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(403);
    }

    private static Stream<Arguments> provideInvalidLoginRequests() {
        return Stream.of(
                Arguments.of(new LoginRequest("", "password")),
                Arguments.of(new LoginRequest("user", ""))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEditUserDTOs")
    void shouldFailToRenameUserWithInvalidData(EditUserDTO dto) {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(dto)
                .pathParam("id", aniaId)
                .when()
                .patch("/api/v1/user/id/{id}/rename")
                .then()
                .statusCode(400);
    }

    private static Stream<Arguments> provideInvalidEditUserDTOs() {
        return Stream.of(
                Arguments.of(new EditUserDTO("")),
                Arguments.of(new EditUserDTO("a"))
        );
    }

    @Test
    void shouldGetAllUsers() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/v1/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("username", hasItems("admin", "anna", "marek"));
    }

    @Test
    void shouldUpdateUser() {
        Response getResponse = given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", aniaId)
                .when()
                .get("/api/v1/user/id/{id}")
                .then()
                .statusCode(200)
                .extract().response();

        String currentEtag = getResponse.getHeader("ETag");
        assertThat(currentEtag).isNotNull();

        EditUserDTO newName = new EditUserDTO("Anna Maria");
        given()
                .header("Authorization", "Bearer " + adminToken)
                .header("If-Match", currentEtag)
                .contentType(ContentType.JSON)
                .body(newName)
                .pathParam("id", aniaId)
                .when()
                .patch("/api/v1/user/id/{id}/rename")
                .then()
                .statusCode(204);

        UserDTO user = given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", aniaId)
                .when()
                .get("/api/v1/user/id/{id}")
                .then()
                .statusCode(200)
                .extract().as(UserDTO.class);

        assertThat(user.name()).isEqualTo("Anna Maria");
    }

    @Test
    void shouldDeleteUser() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", marekId)
                .when()
                .delete("/api/v1/user/id/{id}")
                .then()
                .statusCode(200);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", marekId)
                .when()
                .get("/api/v1/user/id/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldFailToDeleteUserWithoutAdminRole() {
        String clientToken = loginAndGetToken("anna", "password");
        given()
                .header("Authorization", "Bearer " + clientToken)
                .pathParam("id", marekId)
                .when()
                .delete("/api/v1/user/id/{id}")
                .then()
                .statusCode(403);
    }

    @Test
    void shouldFailToRegisterUserWithExistingUsername() {
        RegisterRequest existingUser = new RegisterRequest("anna", "newpassword123", "Inna Anna");
        given()
                .contentType(ContentType.JSON)
                .body(existingUser)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(409);
    }
}
