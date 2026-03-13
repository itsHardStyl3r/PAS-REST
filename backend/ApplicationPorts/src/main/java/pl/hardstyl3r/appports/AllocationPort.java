package pl.hardstyl3r.appports;

import pl.hardstyl3r.pas.v1.objects.Allocation;

import java.util.List;
import java.util.Optional;

public interface AllocationPort {
    List<Allocation> findAll();

    Optional<Allocation> findById(String id);

    Allocation save(Allocation allocation);

    void deleteById(String id);

    boolean existsByResourceIdAndEndTimeIsNull(String resourceId);

    List<Allocation> findByUserId(String userId);

    List<Allocation> findByResourceId(String resourceId);
}