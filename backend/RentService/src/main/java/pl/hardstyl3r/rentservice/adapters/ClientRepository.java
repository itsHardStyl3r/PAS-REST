package pl.hardstyl3r.rentservice.adapters;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClientRepository {

    private final MongoTemplate mongoTemplate;

    public ClientRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<ClientEnt> findAll() {
        return mongoTemplate.findAll(ClientEnt.class);
    }

    public Optional<ClientEnt> findById(String id) {
        if (!ObjectId.isValid(id)) return Optional.empty();
        return Optional.ofNullable(mongoTemplate.findById(id, ClientEnt.class));
    }

    public Optional<ClientEnt> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return Optional.ofNullable(mongoTemplate.findOne(query, ClientEnt.class));
    }

    public ClientEnt save(ClientEnt client) {
        return mongoTemplate.save(client);
    }

    public void deleteById(String id) {
        if (ObjectId.isValid(id)) {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, ClientEnt.class);
        }
    }
}
