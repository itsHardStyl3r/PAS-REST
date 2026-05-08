package pl.hardstyl3r.userservice.adapters;

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

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class MongoInit implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final MongoDatabase mongoDatabase;
    private final String usersCollectionName;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(MongoInit.class);

    @Autowired
    public MongoInit(MongoTemplate mongoTemplate,
                     MongoDatabase mongoDatabase,
                     UserRepository userRepository,
                     PasswordEncoder passwordEncoder,
                     @Value("${pas.mongodb.collection.users}") String usersCollectionName) {
        this.mongoTemplate = mongoTemplate;
        this.mongoDatabase = mongoDatabase;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersCollectionName = usersCollectionName;
    }

    @Override
    public void run(String... args) {
        initUsers();
    }

    private void createCollectionWithSchemaValidation(String collectionName, Document validator) {
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
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
                                .append("username", new Document("bsonType", "string"))
                                .append("name", new Document("bsonType", "string"))
                                .append("password", new Document("bsonType", "string"))
                                .append("active", new Document("bsonType", "bool"))
                                .append("role", new Document("bsonType", "string"))
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
        logger.info("UserService: initialized {} users.", users.size());
    }
}
