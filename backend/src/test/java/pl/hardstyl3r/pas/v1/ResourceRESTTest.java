package pl.hardstyl3r.pas.v1;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
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
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.dto.EditResourceDTO;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.objects.resources.Newspaper;
import pl.hardstyl3r.pas.v1.objects.resources.Periodical;

import java.util.Arrays;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResourceRESTTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private int port;

    @Value("${pas.mongodb.collection.resources}")
    private String collectionName;

    private String bookId;
    private String periodicalId;
    private String newspaperId;

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
        MongoCollection<Document> resourcesCollection = mongoTemplate.createCollection(collectionName);

        Document book1 = new Document("_class", Book.class.getName())
                .append("name", "Morderstwo w Orient Expressie")
                .append("description", "Herkules Poirot po rozwiązaniu sprawy kryminalnej w Azji wraca do Europy.")
                .append("author", "Agatha Christie")
                .append("isbn", "9788327159779");

        Document book2 = new Document("_class", Book.class.getName())
                .append("name", "Poirot prowadzi śledztwo")
                .append("description", "Herkules Poirot łapie przestępców, choć jego samego złapała grypa.")
                .append("author", "Agatha Christie")
                .append("isbn", "9788327157188");

        Document periodical = new Document("_class", Periodical.class.getName())
                .append("name", "CD-Action")
                .append("description", "Magazyn o grach komputerowych")
                .append("issueNumber", 320);

        Document newspaper = new Document("_class", Newspaper.class.getName())
                .append("name", "Gazeta Wyborcza")
                .append("description", "Gazeta")
                .append("releaseDate", "17-11-2025");

        resourcesCollection.insertMany(Arrays.asList(book1, book2, periodical, newspaper));

        this.bookId = Objects.requireNonNull(resourcesCollection.find(Filters.eq("name", "Morderstwo w Orient Expressie")).first()).getObjectId("_id").toHexString();
        this.periodicalId = Objects.requireNonNull(resourcesCollection.find(Filters.eq("name", "CD-Action")).first()).getObjectId("_id").toHexString();
        this.newspaperId = Objects.requireNonNull(resourcesCollection.find(Filters.eq("name", "Gazeta Wyborcza")).first()).getObjectId("_id").toHexString();
    }

    @Test
    void shouldGetAllResources() {
        given()
                .when()
                .get("/api/v1/resources")
                .then()
                .statusCode(200)
                .body("$", hasSize(4))
                .body("name", hasItems("Morderstwo w Orient Expressie", "Poirot prowadzi śledztwo", "CD-Action", "Gazeta Wyborcza"));
    }

    @Test
    void shouldGetResourceById() {
        given()
                .pathParam("id", bookId)
                .when()
                .get("/api/v1/resources/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Morderstwo w Orient Expressie"))
                .body("author", equalTo("Agatha Christie"));
    }

    @Test
    void shouldCreateBook() {
        CreateResourceDTO newBook = new CreateResourceDTO("book", "Dune", "Sci-fi classic", "Frank Herbert", "978-0441013593", null, null);

        given()
                .contentType(ContentType.JSON)
                .body(newBook)
                .when()
                .post("/api/v1/resources")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Dune"))
                .body("author", equalTo("Frank Herbert"));
    }

    @Test
    void shouldUpdateBook() {
        EditResourceDTO updatedBookData = new EditResourceDTO("Murder on the Orient Express", "Updated description", "Agatha Christie", "978-0007119318", null, null);

        given()
                .contentType(ContentType.JSON)
                .body(updatedBookData)
                .pathParam("id", bookId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Murder on the Orient Express"))
                .body("isbn", equalTo("978-0007119318"));
    }

    @Test
    void shouldUpdatePeriodical() {
        EditResourceDTO updatedPeriodicalData = new EditResourceDTO("CD-Action Nowe Pokolenie", "Updated description", null, null, 321, null);

        given()
                .contentType(ContentType.JSON)
                .body(updatedPeriodicalData)
                .pathParam("id", periodicalId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("CD-Action Nowe Pokolenie"))
                .body("issueNumber", equalTo(321));
    }

    @Test
    void shouldDeleteResource() {
        given()
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
        EditResourceDTO updatedData = new EditResourceDTO("Non-existent", "desc", "author", "isbn", null, null);

        given()
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
                .contentType(ContentType.JSON)
                .body(invalidUpdate)
                .pathParam("id", bookId)
                .when()
                .put("/api/v1/resources/{id}")
                .then()
                .statusCode(400);
    }
}
