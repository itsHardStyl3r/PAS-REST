package pl.hardstyl3r.pas.v1.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.exceptions.*;
import pl.hardstyl3r.pas.v1.repositories.AllocationRepository;
import pl.hardstyl3r.pas.v1.repositories.ResourceRepository;
import pl.hardstyl3r.pas.v1.repositories.UserRepository;
import pl.hardstyl3r.repoadapters.objects.AllocationEnt;
import pl.hardstyl3r.repoadapters.objects.UserEnt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public AllocationService(AllocationRepository allocationRepository, UserRepository userRepository, ResourceRepository resourceRepository) {
        this.allocationRepository = allocationRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    public List<AllocationEnt> findAll() {
        return allocationRepository.findAll();
    }

    public Optional<AllocationEnt> findById(String id) {
        return allocationRepository.findById(id);
    }

    public AllocationEnt createAllocation(String userId, String resourceId) {
        UserEnt user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        if (!user.isActive()) {
            throw new UserNotActiveException("Cannot create allocation. User with id " + userId + " is not active.");
        }

        resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + resourceId + " not found."));

        if (allocationRepository.existsByResourceIdAndEndTimeIsNull(resourceId)) {
            throw new ResourceInUseException("Cannot create allocation. Resource with id " + resourceId + " is already allocated.");
        }

        AllocationEnt allocation = new AllocationEnt(userId, resourceId);
        return allocationRepository.save(allocation);
    }

    public AllocationEnt endAllocation(String id) {
        if (id == null || id.isBlank()) {
            throw new InputValidationException("Allocation ID cannot be blank.");
        }
        AllocationEnt allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));

        if (allocation.getEndTime() != null) {
            throw new AllocationException("Allocation with id " + id + " has already been ended.");
        }

        allocation.setEndTime(LocalDateTime.now());
        return allocationRepository.save(allocation);
    }

    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            throw new InputValidationException("Allocation ID cannot be blank.");
        }
        AllocationEnt allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));

        if (allocation.getEndTime() != null) {
            throw new AllocationException("Cannot delete an ended allocation.");
        }
        allocationRepository.deleteById(id);
    }

    public List<AllocationEnt> getCurrentAllocationsForUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InputValidationException("User ID cannot be blank.");
        }
        return allocationRepository.findByUserId(userId).stream()
                .filter(a -> a.getEndTime() == null)
                .collect(Collectors.toList());
    }

    public List<AllocationEnt> getPastAllocationsForUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InputValidationException("User ID cannot be blank.");
        }
        return allocationRepository.findByUserId(userId).stream()
                .filter(a -> a.getEndTime() != null)
                .collect(Collectors.toList());
    }

    public List<AllocationEnt> getCurrentAllocationsForResource(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            throw new InputValidationException("Resource ID cannot be blank.");
        }
        return allocationRepository.findByResourceId(resourceId).stream()
                .filter(a -> a.getEndTime() == null)
                .collect(Collectors.toList());
    }

    public List<AllocationEnt> getPastAllocationsForResource(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            throw new InputValidationException("Resource ID cannot be blank.");
        }
        return allocationRepository.findByResourceId(resourceId).stream()
                .filter(a -> a.getEndTime() != null)
                .collect(Collectors.toList());
    }
}
