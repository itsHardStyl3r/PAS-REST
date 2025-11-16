package pl.hardstyl3r.pas.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.services.AllocationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping
    public List<Allocation> getAllAllocations() {
        return allocationService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public Allocation getAllocationById(@PathVariable String id) {
        return allocationService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Allocation> createAllocation(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String resourceId = payload.get("resourceId");
        Allocation createdAllocation = allocationService.createAllocation(userId, resourceId);
        return ResponseEntity.ok(createdAllocation);
    }

    @PostMapping("/{id}/end")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Allocation> endAllocation(@PathVariable String id) {
        Allocation endedAllocation = allocationService.endAllocation(id);
        return ResponseEntity.ok(endedAllocation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Void> deleteAllocation(@PathVariable String id) {
        allocationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/current")
    public List<Allocation> getCurrentAllocationsForUser(@PathVariable String userId) {
        return allocationService.getCurrentAllocationsForUser(userId);
    }

    @GetMapping("/user/{userId}/past")
    public List<Allocation> getPastAllocationsForUser(@PathVariable String userId) {
        return allocationService.getPastAllocationsForUser(userId);
    }

    @GetMapping("/resource/{resourceId}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public List<Allocation> getCurrentAllocationsForResource(@PathVariable String resourceId) {
        return allocationService.getCurrentAllocationsForResource(resourceId);
    }

    @GetMapping("/resource/{resourceId}/past")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public List<Allocation> getPastAllocationsForResource(@PathVariable String resourceId) {
        return allocationService.getPastAllocationsForResource(resourceId);
    }
}
