package pl.hardstyl3r.rentservice.adapters;

import com.mongodb.MongoCommandException;
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
import org.springframework.stereotype.Component;
import pl.hardstyl3r.rentservice.adapters.resource.BookEnt;
import pl.hardstyl3r.rentservice.adapters.resource.NewspaperEnt;
import pl.hardstyl3r.rentservice.adapters.resource.PeriodicalEnt;
import pl.hardstyl3r.rentservice.adapters.resource.ResourceEnt;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class MongoInit implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final MongoDatabase mongoDatabase;
    private final String clientsCollectionName;
    private final String resourcesCollectionName;
    private final String allocationsCollectionName;
    private final ClientRepository clientRepository;
    private final ResourceRepository resourceRepository;
    private final AllocationRepository allocationRepository;
    private static final Logger logger = LoggerFactory.getLogger(MongoInit.class);

    @Autowired
    public MongoInit(MongoTemplate mongoTemplate,
                     MongoDatabase mongoDatabase,
                     ClientRepository clientRepository,
                     ResourceRepository resourceRepository,
                     AllocationRepository allocationRepository,
                     @Value("${pas.mongodb.collection.clients}") String clientsCollectionName,
                     @Value("${pas.mongodb.collection.resources}") String resourcesCollectionName,
                     @Value("${pas.mongodb.collection.allocations}") String allocationsCollectionName) {
        this.mongoTemplate = mongoTemplate;
        this.mongoDatabase = mongoDatabase;
        this.clientRepository = clientRepository;
        this.resourceRepository = resourceRepository;
        this.allocationRepository = allocationRepository;
        this.clientsCollectionName = clientsCollectionName;
        this.resourcesCollectionName = resourcesCollectionName;
        this.allocationsCollectionName = allocationsCollectionName;
    }

    @Override
    public void run(String... args) {
        initClients();
        initResources();
        initAllocations();
    }

    private void createCollectionWithSchemaValidation(String collectionName, Document validator) {
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
        ValidationOptions validationOptions = new ValidationOptions().validator(validator);
        try {
            mongoDatabase.createCollection(collectionName, new CreateCollectionOptions().validationOptions(validationOptions));
        } catch (MongoCommandException e) {
            if (e.getCode() == 48) logger.warn("Collection {} already exists.", collectionName);
            else throw e;
        }
    }

    private void initClients() {
        Document clientSchema = new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", Arrays.asList("username", "active", "role"))
                        .append("properties", new Document()
                                .append("username", new Document("bsonType", "string"))
                                .append("active", new Document("bsonType", "bool"))
                                .append("role", new Document("bsonType", "string"))
                        )
        );
        createCollectionWithSchemaValidation(clientsCollectionName, clientSchema);
        mongoTemplate.getCollection(clientsCollectionName)
                .createIndex(Indexes.ascending("username"), new IndexOptions().unique(true));

        ClientEnt rwalczak = new ClientEnt("rwalczak", false, ClientEntRole.CLIENT);
        rwalczak.setId("60c72b2f9b1e8a3f3c8e4b1a");

        ClientEnt ewisniewska = new ClientEnt("ewisniewska", true, ClientEntRole.CLIENT);
        ewisniewska.setId("60c72b2f9b1e8a3f3c8e4b1b");

        ClientEnt zchmielewska = new ClientEnt("zchmielewska", true, ClientEntRole.CLIENT);
        zchmielewska.setId("60c72b2f9b1e8a3f3c8e4b1c");

        ClientEnt ksawicka = new ClientEnt("ksawicka", true, ClientEntRole.CLIENT);
        ksawicka.setId("60c72b2f9b1e8a3f3c8e4b1d");

        ClientEnt mzawadzki = new ClientEnt("mzawadzki", false, ClientEntRole.CLIENT);
        mzawadzki.setId("60c72b2f9b1e8a3f3c8e4b1e");

        ClientEnt admin = new ClientEnt("admin", true, ClientEntRole.ADMIN);
        admin.setId("60c72b2f9b1e8a3f3c8e4b1f");

        ClientEnt resourceManager = new ClientEnt("resource", true, ClientEntRole.RESOURCE_MANAGER);
        resourceManager.setId("60c72b2f9b1e8a3f3c8e4b20");

        List<ClientEnt> clients = Arrays.asList(rwalczak, ewisniewska, zchmielewska, ksawicka, mzawadzki, admin, resourceManager);
        clients.forEach(clientRepository::save);
        logger.info("RentService: initialized {} clients.", clients.size());
    }

    private void initResources() {
        Document resourceSchema = new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", Arrays.asList("name", "description"))
                        .append("properties", new Document()
                                .append("name", new Document("bsonType", "string"))
                                .append("description", new Document("bsonType", "string"))
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
        logger.info("RentService: initialized {} resources.", resources.size());
    }

    private void initAllocations() {
        Document allocationSchema = new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required", Arrays.asList("userId", "resourceId", "startTime"))
                        .append("properties", new Document()
                                .append("userId", new Document("bsonType", "string"))
                                .append("resourceId", new Document("bsonType", "string"))
                                .append("startTime", new Document("bsonType", "date"))
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

        logger.info("RentService: initialized 2 allocations.");
    }
}
