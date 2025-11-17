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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.objects.resources.Newspaper;
import pl.hardstyl3r.pas.v1.objects.resources.Periodical;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;
import pl.hardstyl3r.pas.v1.repositories.ResourceRepository;
import pl.hardstyl3r.pas.v1.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class MongoInit implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final String usersCollectionName;
    private final String resourcesCollectionName;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(MongoInit.class);

    @Autowired
    public MongoInit(MongoTemplate mongoTemplate,
                     UserRepository userRepository,
                     ResourceRepository resourceRepository,
                     PasswordEncoder passwordEncoder,
                     @Value("${pas.mongodb.collection.resources}") String resourcesCollectionName,
                     @Value("${pas.mongodb.collection.users}") String usersCollectionName) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
        this.passwordEncoder = passwordEncoder;
        this.resourcesCollectionName = resourcesCollectionName;
        this.usersCollectionName = usersCollectionName;
    }

    @Override
    public void run(String... args) {
        initUsers();
        initResources();
    }

    private void initUsers() {
        mongoTemplate.dropCollection(usersCollectionName);
        MongoCollection<Document> usersCollection = mongoTemplate.getCollection(usersCollectionName);
        usersCollection.createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        User rwalczak = new User("rwalczak", passwordEncoder.encode("password"), "Rafał Walczak", false);
        rwalczak.setId("60c72b2f9b1e8a3f3c8e4b1a");
        rwalczak.setRole(UserRole.CLIENT);

        User ewisniewska = new User("ewisniewska", passwordEncoder.encode("password"), "Edyta Wiśniewska", true);
        ewisniewska.setId("60c72b2f9b1e8a3f3c8e4b1b");
        ewisniewska.setRole(UserRole.CLIENT);

        User zchmielewska = new User("zchmielewska", passwordEncoder.encode("password"), "Zofia Chmielewska", true);
        zchmielewska.setId("60c72b2f9b1e8a3f3c8e4b1c");
        zchmielewska.setRole(UserRole.CLIENT);

        User ksawicka = new User("ksawicka", passwordEncoder.encode("password"), "Kinga Sawicka", true);
        ksawicka.setId("60c72b2f9b1e8a3f3c8e4b1d");
        ksawicka.setRole(UserRole.CLIENT);

        User mzawadzki = new User("mzawadzki", passwordEncoder.encode("password"), "Mieczysław Zawadzki", false);
        mzawadzki.setId("60c72b2f9b1e8a3f3c8e4b1e");
        mzawadzki.setRole(UserRole.CLIENT);

        User admin = new User("admin", passwordEncoder.encode("password"), "Admin User", true);
        admin.setId("60c72b2f9b1e8a3f3c8e4b1f");
        admin.setRole(UserRole.ADMIN);

        User resourceManager = new User("resource", passwordEncoder.encode("password"), "Resource Manager", true);
        resourceManager.setId("60c72b2f9b1e8a3f3c8e4b20");
        resourceManager.setRole(UserRole.RESOURCE_MANAGER);

        List<User> users = Arrays.asList(rwalczak, ewisniewska, zchmielewska, ksawicka, mzawadzki, admin, resourceManager);

        users.forEach(userRepository::save);
        logger.info("Database has been initialized with {} users.", users.size());
    }

    private void initResources() {
        mongoTemplate.dropCollection(resourcesCollectionName);

        List<Resource> resources = Arrays.asList(
                new Book("60c72b2f9b1e8a3f3c8e4b2a", "Morderstwo w Orient Expressie", "Herkules Poirot po rozwiązaniu sprawy kryminalnej w Azji wraca do Europy.", "Agatha Christie", "9788327159779"),
                new Book("60c72b2f9b1e8a3f3c8e4b2b", "Poirot prowadzi śledztwo", "Herkules Poirot łapie przestępców, choć jego samego złapała grypa.", "Agatha Christie", "9788327157188"),
                new Periodical("60c72b2f9b1e8a3f3c8e4b2c", "CD-Action", "Magazyn o grach komputerowych", 320),
                new Newspaper("60c72b2f9b1e8a3f3c8e4b2d", "Gazeta Wyborcza", "Gazeta", "2025-11-17")
        );

        resources.forEach(resourceRepository::save);
        logger.info("Database has been initialized with {} resources.", resources.size());
    }
}
