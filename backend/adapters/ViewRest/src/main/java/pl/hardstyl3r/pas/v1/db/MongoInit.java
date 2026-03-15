package pl.hardstyl3r.pas.v1.db;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.hardstyl3r.repoadapters.objects.*;
import pl.hardstyl3r.repoadapters.objects.resources.BookEnt;
import pl.hardstyl3r.repoadapters.objects.resources.NewspaperEnt;
import pl.hardstyl3r.repoadapters.objects.resources.PeriodicalEnt;
import pl.hardstyl3r.repoadapters.objects.resources.ResourceEnt;
import pl.hardstyl3r.pas.v1.repositories.AllocationRepository;
import pl.hardstyl3r.pas.v1.repositories.ResourceRepository;
import pl.hardstyl3r.pas.v1.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class MongoInit implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final MongoDatabase mongoDatabase;
    private final String usersCollectionName;
    private final String resourcesCollectionName;
    private final String allocationsCollectionName;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final AllocationRepository allocationRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(MongoInit.class);

    @Autowired
    public MongoInit(MongoTemplate mongoTemplate,
                     MongoDatabase mongoDatabase,
                     UserRepository userRepository,
                     ResourceRepository resourceRepository,
                     AllocationRepository allocationRepository,
                     PasswordEncoder passwordEncoder,
                     @Value("${pas.mongodb.collection.resources}") String resourcesCollectionName,
                     @Value("${pas.mongodb.collection.users}") String usersCollectionName,
                     @Value("${pas.mongodb.collection.allocations}") String allocationsCollectionName) {
        this.mongoTemplate = mongoTemplate;
        this.mongoDatabase = mongoDatabase;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.allocationRepository = allocationRepository;
        this.passwordEncoder = passwordEncoder;
        this.resourcesCollectionName = resourcesCollectionName;
        this.usersCollectionName = usersCollectionName;
        this.allocationsCollectionName = allocationsCollectionName;
    }

    @Override
    public void run(String... args) {
        initUsers();
        initResources();
        initAllocations();
    }

    private void createCollectionWithSchemaValidation(String collectionName, Document validator) {
        mongoTemplate.dropCollection(collectionName);
        ValidationOptions validationOptions = new ValidationOptions().validator(validator);
        try {
            mongoDatabase.createCollection(collectionName, new CreateCollectionOptions().validationOptions(validationOptions));
        } catch (MongoCommandException e) {
            if (e.getCode() == 48) logger.warn("Collection {} already exists. Skipping creation.", collectionName);
            else throw e;
        }
    }

    private void initUsers() {
        Document userSchema = new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", Arrays.asList("username", "name", "password", "active", "role"))
                        .append("properties", new Document()
                                .append("username", new Document("bsonType", "string").append("description", "must be a string and is required"))
                                .append("name", new Document("bsonType", "string").append("description", "must be a string and is required"))
                                .append("password", new Document("bsonType", "string").append("description", "must be a string and is required"))
                                .append("active", new Document("bsonType", "bool").append("description", "must be a boolean and is required"))
                                .append("role", new Document("bsonType", "string").append("description", "must be a string and is required"))
                        )
        );
        createCollectionWithSchemaValidation(usersCollectionName, userSchema);
        MongoCollection<Document> usersCollection = mongoTemplate.getCollection(usersCollectionName);
        usersCollection.createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        UserEnt rwalczak = new UserEnt("rwalczak", passwordEncoder.encode("password"), "Rafał Walczak", false);
        rwalczak.setId("60c72b2f9b1e8a3f3c8e4b1a");
        rwalczak.setRole(UserEntRole.CLIENT);

        UserEnt ewisniewska = new UserEnt("ewisniewska", passwordEncoder.encode("password"), "Edyta Wiśniewska", true);
        ewisniewska.setId("60c72b2f9b1e8a3f3c8e4b1b");
        ewisniewska.setRole(UserEntRole.CLIENT);

        UserEnt zchmielewska = new UserEnt("zchmielewska", passwordEncoder.encode("password"), "Zofia Chmielewska", true);
        zchmielewska.setId("60c72b2f9b1e8a3f3c8e4b1c");
        zchmielewska.setRole(UserEntRole.CLIENT);

        UserEnt ksawicka = new UserEnt("ksawicka", passwordEncoder.encode("password"), "Kinga Sawicka", true);
        ksawicka.setId("60c72b2f9b1e8a3f3c8e4b1d");
        ksawicka.setRole(UserEntRole.CLIENT);

        UserEnt mzawadzki = new UserEnt("mzawadzki", passwordEncoder.encode("password"), "Mieczysław Zawadzki", false);
        mzawadzki.setId("60c72b2f9b1e8a3f3c8e4b1e");
        mzawadzki.setRole(UserEntRole.CLIENT);

        UserEnt admin = new UserEnt("admin", passwordEncoder.encode("password"), "Admin User", true);
        admin.setId("60c72b2f9b1e8a3f3c8e4b1f");
        admin.setRole(UserEntRole.ADMIN);

        UserEnt resourceManager = new UserEnt("resource", passwordEncoder.encode("password"), "Resource Manager", true);
        resourceManager.setId("60c72b2f9b1e8a3f3c8e4b20");
        resourceManager.setRole(UserEntRole.RESOURCE_MANAGER);

        List<UserEnt> users = Arrays.asList(rwalczak, ewisniewska, zchmielewska, ksawicka, mzawadzki, admin, resourceManager);

        users.forEach(userRepository::save);
        logger.info("Database has been initialized with {} users.", users.size());
    }

    private void initResources() {
        Document resourceSchema = new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", Arrays.asList("name", "description"))
                        .append("properties", new Document()
                                .append("name", new Document("bsonType", "string").append("description", "must be a string and is required"))
                                .append("description", new Document("bsonType", "string").append("description", "must be a string and is required"))
                        )
        );
        createCollectionWithSchemaValidation(resourcesCollectionName, resourceSchema);

        List<ResourceEnt> resources = Arrays.asList(
                new BookEnt("60c72b2f9b1e8a3f3c8e4b2a", "Morderstwo w Orient Expressie", "Herkules Poirot po rozwiązaniu sprawy kryminalnej w Azji wraca do Europy.", "Agatha Christie", "9788327159779"),
                new BookEnt("60c72b2f9b1e8a3f3c8e4b2b", "Poirot prowadzi śledztwo", "Herkules Poirot łapie przestępców, choć jego samego złapała grypa.", "Agatha Christie", "9788327157188"),
                new PeriodicalEnt("60c72b2f9b1e8a3f3c8e4b2c", "CD-Action", "Magazyn o grach komputerowych", 320),
                new NewspaperEnt("60c72b2f9b1e8a3f3c8e4b2d", "Gazeta Wyborcza", "Gazeta", "2025-11-17")
        );

        resources.forEach(resourceRepository::save);
        logger.info("Database has been initialized with {} resources.", resources.size());
    }

    private void initAllocations() {
        Document allocationSchema = new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", Arrays.asList("userId", "resourceId", "startTime"))
                        .append("properties", new Document()
                                .append("userId", new Document("bsonType", "string").append("description", "must be a string and is required"))
                                .append("resourceId", new Document("bsonType", "string").append("description", "must be a string and is required"))
                                .append("startTime", new Document("bsonType", "date").append("description", "must be a date and is required"))
                                .append("endTime", new Document("bsonType", "date").append("description", "must be a date and is optional"))
                        )
        );
        createCollectionWithSchemaValidation(allocationsCollectionName, allocationSchema);

        AllocationEnt activeAllocation = new AllocationEnt("60c72b2f9b1e8a3f3c8e4b1d", "60c72b2f9b1e8a3f3c8e4b2c");
        activeAllocation.setId("692c9fe56f86670cdd4f55f0");
        activeAllocation.setStartTime(LocalDateTime.now().minusDays(1));
        allocationRepository.save(activeAllocation);

        AllocationEnt pastAllocation = new AllocationEnt("60c72b2f9b1e8a3f3c8e4b1a", "60c72b2f9b1e8a3f3c8e4b2a");
        pastAllocation.setId("692c9fe56f86670cdd4f55f1");
        pastAllocation.setStartTime(LocalDateTime.now().minusDays(10));
        pastAllocation.setEndTime(LocalDateTime.now().minusDays(5));
        allocationRepository.save(pastAllocation);

        logger.info("Database has been initialized with 2 allocations.");
    }
}
