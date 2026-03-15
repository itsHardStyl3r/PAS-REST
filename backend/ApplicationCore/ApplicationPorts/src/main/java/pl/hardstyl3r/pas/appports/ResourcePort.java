package pl.hardstyl3r.pas.appports;

import pl.hardstyl3r.pas.v1.objects.resources.Resource;

import java.util.List;
import java.util.Optional;

public interface ResourcePort {
    List<Resource> findAll();

    Optional<Resource> findById(String id);

    Resource save(Resource resource);

    void deleteById(String id);

    boolean existsById(String id);
}