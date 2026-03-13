package pl.hardstyl3r.pas.v1.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.hardstyl3r.pas.v1.objects.User;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class UserRepository {

    private final MongoTemplate mongoTemplate;

    public UserRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<User> findAll() {
        return mongoTemplate.findAll(User.class);
    }

    public Optional<User> findById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(mongoTemplate.findById(id, User.class));
    }

    public Optional<User> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
    }

    public boolean existsByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.exists(query, User.class);
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public void deleteById(String id) {
        if (ObjectId.isValid(id)) {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, User.class);
        }
    }

    public void update(User user) {
        mongoTemplate.save(user);
    }

    public List<User> findByUsernameContaining(String search) {
        Pattern regex = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        Query query = new Query(Criteria.where("username").regex(regex));
        return mongoTemplate.find(query, User.class);
    }
}
