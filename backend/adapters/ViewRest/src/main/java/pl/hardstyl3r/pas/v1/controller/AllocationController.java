package pl.hardstyl3r.pas.v1.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.dto.AllocationRequest;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserValidationException;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.viewports.AllocationViewPort;
import pl.hardstyl3r.pas.v1.viewports.UserViewPort;

import java.util.List;

@RestController
@RequestMapping("/api/v1/allocations")
@CrossOrigin(origins = "http://localhost:5173")
public class AllocationController {

    private final AllocationViewPort allocationViewPort;
    private final UserViewPort userViewPort;

    public AllocationController(AllocationViewPort allocationViewPort, UserViewPort userViewPort) {
        this.allocationViewPort = allocationViewPort;
        this.userViewPort = userViewPort;
    }

    @GetMapping
    public List<Allocation> getAllAllocations() {
        return allocationViewPort.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public Allocation getAllocationById(@PathVariable String id) {
        return allocationViewPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'CLIENT')")
    public ResponseEntity<Allocation> createAllocation(@Valid @RequestBody AllocationRequest allocationRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        boolean isClient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        String targetUserId;

        if (isClient) {
            User currentUser = userViewPort.findUserByUsername(currentUsername)
                    .orElseThrow(() -> new UserNotFoundException("Nie znaleziono zalogowanego użytkownika."));
            targetUserId = currentUser.getId();
        } else {
            if (allocationRequest.userId() == null || allocationRequest.userId().isBlank()) {
                throw new UserValidationException("Admin/Manager musi podać ID użytkownika docelowego.");
            }
            targetUserId = allocationRequest.userId();
        }

        Allocation createdAllocation = allocationViewPort.createAllocation(targetUserId, allocationRequest.resourceId());
        return ResponseEntity.ok(createdAllocation);
    }

    @PostMapping("/{id}/end")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Allocation> endAllocation(@PathVariable String id) {
        Allocation endedAllocation = allocationViewPort.endAllocation(id);
        return ResponseEntity.ok(endedAllocation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Void> deleteAllocation(@PathVariable String id) {
        allocationViewPort.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/current")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'CLIENT')")
    public List<Allocation> getCurrentAllocationsForUser(@PathVariable String userId) {
        validateAccessToUserData(userId);
        return allocationViewPort.getCurrentAllocationsForUser(userId);
    }

    @GetMapping("/user/{userId}/past")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER', 'CLIENT')")
    public List<Allocation> getPastAllocationsForUser(@PathVariable String userId) {
        validateAccessToUserData(userId);
        return allocationViewPort.getPastAllocationsForUser(userId);
    }

    private void validateAccessToUserData(String requestedUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isClient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if (isClient) {
            String currentUsername = auth.getName();
            User currentUser = userViewPort.findUserByUsername(currentUsername)
                    .orElseThrow(() -> new UserNotFoundException("Nie znaleziono zalogowanego użytkownika."));

            if (!currentUser.getId().equals(requestedUserId)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Nie masz uprawnień do przeglądania alokacji innego użytkownika."
                );
            }
        }
    }
}