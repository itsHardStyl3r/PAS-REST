package pl.hardstyl3r.rentservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.appports.UserPort;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserValidationException;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.rentservice.domain.Client;
import pl.hardstyl3r.rentservice.domain.ClientMapper;
import pl.hardstyl3r.rentservice.dto.AllocationRequest;
import pl.hardstyl3r.rentservice.services.AllocationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/allocations")
@CrossOrigin(origins = "http://localhost:5173")
public class AllocationController {

    private final AllocationService allocationService;
    private final UserPort userPort;

    public AllocationController(AllocationService allocationService, UserPort userPort) {
        this.allocationService = allocationService;
        this.userPort = userPort;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'CLIENT')")
    public ResponseEntity<Allocation> createAllocation(@Valid @RequestBody AllocationRequest allocationRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isClient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        String targetUserId;

        if (isClient) {
            Client currentClient = currentClient(auth.getName());
            targetUserId = currentClient.getId();
        } else {
            if (allocationRequest.userId() == null || allocationRequest.userId().isBlank()) {
                throw new UserValidationException("Admin/Manager musi podać ID użytkownika docelowego.");
            }
            targetUserId = allocationRequest.userId();
        }

        Allocation createdAllocation = allocationService.createAllocation(targetUserId, allocationRequest.resourceId());
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
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'CLIENT')")
    public List<Allocation> getCurrentAllocationsForUser(@PathVariable String userId) {
        validateAccessToUserData(userId);
        return allocationService.getCurrentAllocationsForUser(userId);
    }

    @GetMapping("/user/{userId}/past")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'CLIENT')")
    public List<Allocation> getPastAllocationsForUser(@PathVariable String userId) {
        validateAccessToUserData(userId);
        return allocationService.getPastAllocationsForUser(userId);
    }

    private Client currentClient(String username) {
        return userPort.findByUsername(username)
                .map(ClientMapper::fromUser)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono zalogowanego użytkownika."));
    }

    private void validateAccessToUserData(String requestedUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isClient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if (isClient) {
            Client currentClient = currentClient(auth.getName());
            if (!currentClient.getId().equals(requestedUserId)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Nie masz uprawnień do przeglądania alokacji innego użytkownika."
                );
            }
        }
    }
}
