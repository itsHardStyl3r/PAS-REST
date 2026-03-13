package pl.hardstyl3r.pas.v1.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.hardstyl3r.repoadapters.objects.resources.ResourceEnt;

import java.util.List;
import java.util.Optional;

@Repository
public class ResourceRepository {

    private final MongoTemplate mongoTemplate;

    public ResourceRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<ResourceEnt> findAll() {
        return mongoTemplate.findAll(ResourceEnt.class);
    }

    public Optional<ResourceEnt> findById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        ResourceEnt resource = mongoTemplate.findById(id, ResourceEnt.class);
        return Optional.ofNullable(resource);
    }

    public ResourceEnt save(ResourceEnt resource) {
        return mongoTemplate.save(resource);
    }

    public void deleteById(String id) {
        if (ObjectId.isValid(id)) {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, ResourceEnt.class);
        }
    }

    public boolean existsById(String id) {
        if (!ObjectId.isValid(id)) {
            return false;
        }
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, ResourceEnt.class);
    }
}
