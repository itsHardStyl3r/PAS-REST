package pl.hardstyl3r.repoadapters.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoDatabase mongoDatabase(
            @Value("${spring.data.mongodb.uri}") String connectionString,
            @Value("${spring.data.mongodb.database}") String databaseName) {

        MongoClient mongoClient = MongoClients.create(connectionString);
        return mongoClient.getDatabase(databaseName);
    }
}