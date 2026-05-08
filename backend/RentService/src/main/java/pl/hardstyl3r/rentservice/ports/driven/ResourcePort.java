package pl.hardstyl3r.rentservice.ports.driven;

import pl.hardstyl3r.rentservice.domain.resource.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourcePort {
    List<Resource> findAll();
    Optional<Resource> findById(String id);
    Resource save(Resource resource);
    void deleteById(String id);
    boolean existsById(String id);
}
