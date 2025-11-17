package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
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
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.dto.EditResourceDTO;
import pl.hardstyl3r.pas.v1.dto.LoginRequest;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.objects.resources.Newspaper;
import pl.hardstyl3r.pas.v1.objects.resources.Periodical;

import java.util.Arrays;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResourceRESTTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.resources}")
    private String resourcesCollectionName;
    @Value("${pas.mongodb.collection.users}")
    private String usersCollectionName;

    private String bookId;
    private String periodicalId;
    private String newspaperId;
    private String adminToken;
    private String clientToken;

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

        mongoTemplate.dropCollection(resourcesCollectionName);
        mongoTemplate.dropCollection(usersCollectionName);
        MongoCollection<Document> resourcesCollection = mongoTemplate.createCollection(resourcesCollectionName);
        MongoCollection<Document> usersCollection = mongoTemplate.createCollection(usersCollectionName);

        Document adminUser = new Document("username", "admin").append("password", passwordEncoder.encode("password")).append("role", UserRole.ADMIN.name()).append("active", true);
        Document clientUser = new Document("username", "client").append("password", passwordEncoder.encode("password")).append("role", UserRole.CLIENT.name()).append("active", true);
        usersCollection.insertMany(Arrays.asList(adminUser, clientUser));

        adminToken = loginAndGetToken("admin", "password");
        clientToken = loginAndGetToken("client", "password");

        Document book1 = new Document("_class", Book.class.getName()).append("name", "Morderstwo w Orient Expressie").append("description", "Herkules Poirot...").append("author", "Agatha Christie").append("isbn", "9788327159779");
        Document periodical = new Document("_class", Periodical.class.getName()).append("name", "CD-Action").append("description", "Magazyn...").append("issueNumber", 320);
        Document newspaper = new Document("_class", Newspaper.class.getName()).append("name", "Gazeta Wyborcza").append("description", "Gazeta").append("releaseDate", "2025-11-17");
        resourcesCollection.insertMany(Arrays.asList(book1, periodical, newspaper));

        this.bookId = Objects.requireNonNull(resourcesCollection.find(Filters.eq("name", "Morderstwo w Orient Expressie")).first()).getObjectId("_id").toHexString();
        this.periodicalId = Objects.requireNonNull(resourcesCollection.find(Filters.eq("name", "CD-Action")).first()).getObjectId("_id").toHexString();
        this.newspaperId = Objects.requireNonNull(resourcesCollection.find(Filters.eq("name", "Gazeta Wyborcza")).first()).getObjectId("_id").toHexString();
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
    void shouldGetAllResources() {
        given()
                .when()
                .get("/api/v1/resources")
                .then()
                .statusCode(200)
                .body("$", hasSize(3));
    }

    @Test
    void shouldGetResourceById() {
        given()
                .pathParam("id", bookId)
                .when()
                .get("/api/v1/resources/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Morderstwo w Orient Expressie"));
    }

    @Test
    void shouldCreateBookWithAdminRole() {
        CreateResourceDTO newBook = new CreateResourceDTO("book", "I nie było już nikogo",
                "Tajemniczy gospodarz zaprasza do domu na wyspie dziesięć osób.", "Agatha Christie",
                "9788327165596", null, null);
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(newBook)
                .when()
                .post("/api/v1/resources")
                .then()
                .statusCode(200)
                .body("name", equalTo("I nie było już nikogo"))
                .body("author", equalTo("Agatha Christie"))
                .body("isbn", equalTo("9788327165596"));
    }

    @Test
    void shouldFailToCreateBookWithClientRole() {
        CreateResourceDTO newBook = new CreateResourceDTO("book", "I nie było już nikogo",
                "Tajemniczy gospodarz zaprasza do domu na wyspie dziesięć osób.", "Agatha Christie",
                "9788327165596", null, null);
        given()
                .header("Authorization", "Bearer " + clientToken)
                .contentType(ContentType.JSON)
                .body(newBook)
                .when()
                .post("/api/v1/resources")
                .then()
                .statusCode(403);
    }

    @Test
    void shouldUpdateBook() {
        EditResourceDTO updatedBookData = new EditResourceDTO("Murder on the Orient Express",
                "Nowy description",
                "Agatha Christie", "9788327159779", null, null);
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(updatedBookData)
                .pathParam("id", bookId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Murder on the Orient Express"))
                .body("description", equalTo("Nowy description"));
    }

    @Test
    void shouldDeleteResource() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", newspaperId)
                .when()
                .delete("/api/v1/resources/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", newspaperId)
                .when()
                .get("/api/v1/resources/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn404ForNonExistentResourceOnGet() {
        String nonExistentId = "60c72b2f9b1e8b3b3c8b4567";
        given()
                .pathParam("id", nonExistentId)
                .when()
                .get("/api/v1/resources/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn404ForNonExistentResourceOnPut() {
        String nonExistentId = "60c72b2f9b1e8b3b3c8b4567";
        EditResourceDTO updatedData = new EditResourceDTO("Jakaś", "desc", "author", "isbn", null, null);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(updatedData)
                .pathParam("id", nonExistentId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldReturn400WhenCreatingResourceWithBlankName() {
        CreateResourceDTO invalidResource = new CreateResourceDTO("book", "", "desc", "author", "isbn", null, null);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(invalidResource)
                .when()
                .post("/api/v1/resources")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithoutRequiredFields() {
        CreateResourceDTO invalidBook = new CreateResourceDTO("book", "A Book", "desc", "", null, null, null);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(invalidBook)
                .when()
                .post("/api/v1/resources")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithInvalidData() {
        EditResourceDTO invalidUpdate = new EditResourceDTO("Valid Name", "desc", "", null, null, null);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(invalidUpdate)
                .pathParam("id", bookId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithInvalidIsbn() {
        CreateResourceDTO invalidBook = new CreateResourceDTO("book", "A Book", "A description", "An author", "12345", null, null);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(invalidBook)
                .when()
                .post("/api/v1/resources")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithInvalidIsbn() {
        EditResourceDTO invalidUpdate = new EditResourceDTO("Valid Name", "desc", "author", "invalid-isbn", null, null);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(invalidUpdate)
                .pathParam("id", bookId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenCreatingNewspaperWithInvalidDate() {
        CreateResourceDTO invalidNewspaper = new CreateResourceDTO("newspaper", "A Newspaper", "A description", null, null, null, "2025/11/17");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(invalidNewspaper)
                .when()
                .post("/api/v1/resources")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenUpdatingNewspaperWithInvalidDate() {
        EditResourceDTO invalidUpdate = new EditResourceDTO("Valid Name", "desc", null, null, null, "17-11-2025");

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(invalidUpdate)
                .pathParam("id", newspaperId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(400);
    }
}
