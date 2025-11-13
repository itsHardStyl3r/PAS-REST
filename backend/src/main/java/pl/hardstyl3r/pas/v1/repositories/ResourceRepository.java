package pl.hardstyl3r.pas.v1.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;

import java.util.List;
import java.util.Optional;

@Repository
public class ResourceRepository {

    private final MongoTemplate mongoTemplate;

    public ResourceRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Resource> findAll() {
        return mongoTemplate.findAll(Resource.class);
    }

    public Optional<Resource> findById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        Resource resource = mongoTemplate.findById(id, Resource.class);
        return Optional.ofNullable(resource);
    }

    public Resource save(Resource resource) {
        return mongoTemplate.save(resource);
    }

    public void deleteById(String id) {
        if (ObjectId.isValid(id)) {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, Resource.class);
        }
    }

}
