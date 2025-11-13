package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import pl.hardstyl3r.pas.v1.dto.CreateUserDTO;
import pl.hardstyl3r.pas.v1.dto.EditUserDTO;
import pl.hardstyl3r.pas.v1.dto.UserDTO;

import java.util.Arrays;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRESTTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.users}")
    private String collectionName;

    private String aniaId;
    private String marekId;

    @BeforeAll
    void checkDatabaseConnection() {
        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
        } catch (Exception e) {
            Assumptions.abort("Could not connect to MongoDB. Skipping integration tests. Please ensure docker-compose is running.");
        }
    }

    @BeforeEach
    void setup() {
        RestAssured.port = port;

        mongoTemplate.dropCollection(collectionName);
        mongoTemplate.createCollection(collectionName);
        MongoCollection<Document> usersCollection = mongoTemplate.getCollection(collectionName);
        usersCollection.createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        Document ania = new Document("username", "anna").append("name", "Ania").append("active", true);
        Document marek = new Document("username", "marek").append("name", "Marek").append("active", true);
        usersCollection.insertMany(Arrays.asList(ania, marek));

        Document aniaDoc = usersCollection.find(Filters.eq("username", "anna")).first();
        Document marekDoc = usersCollection.find(Filters.eq("username", "marek")).first();
        this.aniaId = Objects.requireNonNull(aniaDoc).getObjectId("_id").toHexString();
        this.marekId = Objects.requireNonNull(marekDoc).getObjectId("_id").toHexString();
    }

    @Test
    void shouldCreateUser() {
        CreateUserDTO newUser = new CreateUserDTO("kasia", "Katarzyna", true);

        given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/api/v1/user")
                .then()
                .statusCode(200);

        UserDTO createdUser = given()
                .pathParam("username", "kasia")
                .when()
                .get("/api/v1/user/username/{username}")
                .then()
                .statusCode(200)
                .extract().as(UserDTO.class);

        assertThat(createdUser.username()).isEqualTo("kasia");
        assertThat(createdUser.name()).isEqualTo("Katarzyna");
        assertThat(createdUser.id()).isNotNull();
    }

    @Test
    void shouldGetAllUsers() {
        given()
                .when()
                .get("/api/v1/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("username", hasItems("anna", "marek"));
    }

    @Test
    void shouldUpdateUser() {
        EditUserDTO newName = new EditUserDTO("Anna Maria");
        given()
                .contentType(ContentType.JSON)
                .body(newName)
                .pathParam("id", aniaId)
                .when()
                .patch("/api/v1/user/id/{id}/rename")
                .then()
                .statusCode(200);

        given()
                .pathParam("id", aniaId)
                .when()
                .get("/api/v1/user/id/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Anna Maria"));
    }

    @Test
    void shouldDeleteUser() {
        given()
                .pathParam("id", marekId)
                .when()
                .delete("/api/v1/user/id/{id}")
                .then()
                .statusCode(200);

        given()
                .pathParam("id", marekId)
                .when()
                .get("/api/v1/user/id/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn400WhenCreatingUserWithBlankUsername() {
        CreateUserDTO invalidUser = new CreateUserDTO("", "Test", true);

        given()
                .contentType(ContentType.JSON)
                .body(invalidUser)
                .when()
                .post("/api/v1/user")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn409WhenCreatingUserWithExistingUsername() {
        CreateUserDTO duplicateUser = new CreateUserDTO("anna", "Anna Nowa", true);

        given()
                .contentType(ContentType.JSON)
                .body(duplicateUser)
                .when()
                .post("/api/v1/user")
                .then()
                .statusCode(409);
    }
}
