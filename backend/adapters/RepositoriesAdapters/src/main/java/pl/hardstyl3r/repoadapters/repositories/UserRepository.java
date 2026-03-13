package pl.hardstyl3r.repoadapters.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.hardstyl3r.repoadapters.objects.UserEnt;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class UserRepository {

    private final MongoTemplate mongoTemplate;

    public UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<UserEnt> findAll() {
        return mongoTemplate.findAll(UserEnt.class);
    }

    public Optional<UserEnt> findById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(mongoTemplate.findById(id, UserEnt.class));
    }

    public Optional<UserEnt> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return Optional.ofNullable(mongoTemplate.findOne(query, UserEnt.class));
    }

    public boolean existsByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.exists(query, UserEnt.class);
    }

    public UserEnt save(UserEnt user) {
        return mongoTemplate.save(user);
    }

    public void deleteById(String id) {
        if (ObjectId.isValid(id)) {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, UserEnt.class);
        }
    }

    public void update(UserEnt user) {
        mongoTemplate.save(user);
    }

    public List<UserEnt> findByUsernameContaining(String search) {
        Pattern regex = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where("username").regex(regex));
        return mongoTemplate.find(query, UserEnt.class);
    }
}
