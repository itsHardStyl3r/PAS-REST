package pl.hardstyl3r.pas.v1.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class MongoInit implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final String usersCollectionName;
    private static final Logger logger = LoggerFactory.getLogger(MongoInit.class);

    @Autowired
    public MongoInit(MongoTemplate mongoTemplate, @Value("${pas.mongodb.collection.users}") String usersCollectionName) {
        this.mongoTemplate = mongoTemplate;
        this.usersCollectionName = usersCollectionName;
    }

    @Override
    public void run(String... args) {
        mongoTemplate.dropCollection(usersCollectionName);
        MongoCollection<Document> usersCollection = mongoTemplate.createCollection(usersCollectionName);
        usersCollection.createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        // https://www.fakenamegenerator.com/gen-random-pl-pl.php
        List<Document> users = Arrays.asList(
                new Document("username", "rwalczak").append("name", "Rafał Walczak").append("active", false),
                new Document("username", "ewisniewska").append("name", "Edyta Wiśniewska").append("active", true),
                new Document("username", "zchmielewska").append("name", "Zofia Chmielewska").append("active", true),
                new Document("username", "ksawicka").append("name", "Kinga Sawicka").append("active", true),
                new Document("username", "mzawadzki").append("name", "Mieczysław Zawadzki").append("active", false)
        );

        usersCollection.insertMany(users);
        logger.info("Database has been initialized with {} users.", users.size());
    }
}
