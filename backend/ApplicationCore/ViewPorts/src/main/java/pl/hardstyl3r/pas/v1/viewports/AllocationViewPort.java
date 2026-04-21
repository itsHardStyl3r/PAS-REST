package pl.hardstyl3r.pas.v1.viewports;

import pl.hardstyl3r.pas.v1.objects.Allocation;

import java.util.List;
import java.util.Optional;

public interface AllocationViewPort {
    List<Allocation> findAll();

    Optional<Allocation> findById(String id);

    Allocation createAllocation(String userId, String resourceId);

    Allocation endAllocation(String id);

    void deleteById(String id);

    List<Allocation> getCurrentAllocationsForUser(String userId);

    List<Allocation> getPastAllocationsForUser(String userId);
}

