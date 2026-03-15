package pl.hardstyl3r.pas.v1.db;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class MongoConVerifier implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MongoConVerifier.class);

    private final MongoDatabase mongoDatabase;
    private final ConfigurableApplicationContext context;

    public MongoConVerifier(MongoDatabase mongoDatabase, ConfigurableApplicationContext context) {
        this.mongoDatabase = mongoDatabase;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            mongoDatabase.runCommand(new Document("ping", 1));
            logger.info("Successfully connected to MongoDB.");
        } catch (Exception e) {
            logger.error("FATAL: Failed to connect to MongoDB. Shutting down application.", e);
            int exitCode = SpringApplication.exit(context, () -> 1);
            System.exit(exitCode);
        }
    }
}
