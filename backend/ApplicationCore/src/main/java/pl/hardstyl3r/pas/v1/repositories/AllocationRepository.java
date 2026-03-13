package pl.hardstyl3r.pas.v1.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.hardstyl3r.repoadapters.objects.AllocationEnt;

import java.util.List;
import java.util.Optional;

@Repository
public class AllocationRepository {

    private final MongoTemplate mongoTemplate;

    public AllocationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<AllocationEnt> findAll() {
        return mongoTemplate.findAll(AllocationEnt.class);
    }

    public Optional<AllocationEnt> findById(String id) {
        if (!ObjectId.isValid(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(mongoTemplate.findById(id, AllocationEnt.class));
    }

    public AllocationEnt save(AllocationEnt allocation) {
        return mongoTemplate.save(allocation);
    }

    public void deleteById(String id) {
        if (ObjectId.isValid(id)) {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, AllocationEnt.class);
        }
    }

    public boolean existsByResourceIdAndEndTimeIsNull(String resourceId) {
        Query query = new Query(Criteria.where("resourceId").is(resourceId).and("endTime").is(null));
        return mongoTemplate.exists(query, AllocationEnt.class);
    }

    public List<AllocationEnt> findByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, AllocationEnt.class);
    }

    public List<AllocationEnt> findByResourceId(String resourceId) {
        Query query = new Query(Criteria.where("resourceId").is(resourceId));
        return mongoTemplate.find(query, AllocationEnt.class);
    }
}
