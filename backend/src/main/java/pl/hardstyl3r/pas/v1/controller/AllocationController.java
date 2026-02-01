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
import pl.hardstyl3r.pas.v1.services.AllocationService;
import pl.hardstyl3r.pas.v1.services.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/allocations")
@CrossOrigin(origins = "http://localhost:5173")
public class AllocationController {

    private final AllocationService allocationService;
    private final UserService userService;

    public AllocationController(AllocationService allocationService, UserService userService) {
        this.allocationService = allocationService;
        this.userService = userService;
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
        String currentUsername = auth.getName();

        boolean isClient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        String targetUserId;

        if (isClient) {
            User currentUser = userService.findUserByUsername(currentUsername)
                    .orElseThrow(() -> new UserNotFoundException("Nie znaleziono zalogowanego użytkownika."));
            targetUserId = currentUser.getId();
        } else {
            if (allocationRequest.userId() == null || allocationRequest.userId().isBlank()) {
                throw new UserValidationException("Admin/Manager musi podać ID użytkownika docelowego.");
            }

            if (userService.findUserById(allocationRequest.userId()).isEmpty()) {
                throw new UserNotFoundException("Nie znaleziono użytkownika o podanym ID: " + allocationRequest.userId());
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

    private void validateAccessToUserData(String requestedUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isClient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        if (isClient) {
            String currentUsername = auth.getName();
            User currentUser = userService.findUserByUsername(currentUsername)
                    .orElseThrow(() -> new UserNotFoundException("Nie znaleziono zalogowanego użytkownika."));

            if (!currentUser.getId().equals(requestedUserId)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Nie masz uprawnień do przeglądania alokacji innego użytkownika."
                );
            }
        }

    }
}
