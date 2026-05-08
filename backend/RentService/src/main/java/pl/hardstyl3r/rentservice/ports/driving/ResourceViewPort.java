package pl.hardstyl3r.rentservice.ports.driving;

import pl.hardstyl3r.rentservice.domain.resource.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourceViewPort {
    List<Resource> findAll();
    Optional<Resource> findById(String id);
    Resource createResource(CreateResourceCommand command);
    Resource updateResource(String id, EditResourceCommand command);
    void deleteById(String id);
}
