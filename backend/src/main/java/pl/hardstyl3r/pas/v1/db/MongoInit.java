package pl.hardstyl3r.pas.v1.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.objects.resources.Newspaper;
import pl.hardstyl3r.pas.v1.objects.resources.Periodical;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class MongoInit implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final String usersCollectionName;
    private final String resourcesCollectionName;
    private static final Logger logger = LoggerFactory.getLogger(MongoInit.class);

    @Autowired
    public MongoInit(MongoTemplate mongoTemplate,
                     @Value("${pas.mongodb.collection.users}") String usersCollectionName,
                     @Value("${pas.mongodb.collection.resources}") String resourcesCollectionName) {
        this.mongoTemplate = mongoTemplate;
        this.usersCollectionName = usersCollectionName;
        this.resourcesCollectionName = resourcesCollectionName;
    }

    @Override
    public void run(String... args) {
        initUsers();
        initResources();
    }

    private void initUsers() {
        mongoTemplate.dropCollection(usersCollectionName);
        MongoCollection<Document> usersCollection = mongoTemplate.createCollection(usersCollectionName);
        usersCollection.createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        // https://www.fakenamegenerator.com/gen-random-pl-pl.php
        List<Document> users = Arrays.asList(
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b1a")).append("username", "rwalczak").append("name", "Rafał Walczak").append("active", false),
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b1b")).append("username", "ewisniewska").append("name", "Edyta Wiśniewska").append("active", true),
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b1c")).append("username", "zchmielewska").append("name", "Zofia Chmielewska").append("active", true),
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b1d")).append("username", "ksawicka").append("name", "Kinga Sawicka").append("active", true),
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b1e")).append("username", "mzawadzki").append("name", "Mieczysław Zawadzki").append("active", false)
        );

        usersCollection.insertMany(users);
        logger.info("Database has been initialized with {} users.", users.size());
    }

    private void initResources() {
        mongoTemplate.dropCollection(resourcesCollectionName);
        MongoCollection<Document> resourcesCollection = mongoTemplate.createCollection(resourcesCollectionName);

        List<Document> resources = Arrays.asList(
                // https://lubimyczytac.pl/ksiazka/4936102/morderstwo-w-orient-expressie
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b2a"))
                        .append("_class", Book.class.getName())
                        .append("name", "Morderstwo w Orient Expressie")
                        .append("description", "Herkules Poirot po rozwiązaniu sprawy kryminalnej w Azji wraca do Europy.")
                        .append("author", "Agatha Christie")
                        .append("isbn", "9788327159779"),
                // https://lubimyczytac.pl/ksiazka/4806155/poirot-prowadzi-sledztwo
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b2b"))
                        .append("_class", Book.class.getName())
                        .append("name", "Poirot prowadzi śledztwo")
                        .append("description", "Herkules Poirot łapie przestępców, choć jego samego złapała grypa.")
                        .append("author", "Agatha Christie")
                        .append("isbn", "9788327157188"),
                // https://www.cdaction.pl/
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b2c"))
                        .append("_class", Periodical.class.getName())
                        .append("name", "CD-Action")
                        .append("description", "Magazyn o grach komputerowych")
                        .append("issueNumber", 320),
                // https://wyborcza.pl/
                new Document("_id", new ObjectId("60c72b2f9b1e8a3f3c8e4b2d"))
                        .append("_class", Newspaper.class.getName())
                        .append("name", "Gazeta Wyborcza")
                        .append("description", "Gazeta")
                        .append("releaseDate", "17-11-2025")
        );

        resourcesCollection.insertMany(resources);
        logger.info("Database has been initialized with {} resources.", resources.size());
    }
}
