package pl.hardstyl3r.pas.v1.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.exceptions.*;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.repositories.AllocationRepository;
import pl.hardstyl3r.pas.v1.repositories.ResourceRepository;
import pl.hardstyl3r.pas.v1.repositories.UserRepository;

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

    public List<Allocation> findAll() {
        return allocationRepository.findAll();
    }

    public Optional<Allocation> findById(String id) {
        return allocationRepository.findById(id);
    }

    public Allocation createAllocation(String userId, String resourceId) {
        if (userId == null || userId.isBlank()) {
            throw new AllocationException("User ID cannot be blank.");
        }
        if (resourceId == null || resourceId.isBlank()) {
            throw new AllocationException("Resource ID cannot be blank.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        if (!user.isActive()) {
            throw new AllocationException("Cannot create allocation. User with id " + userId + " is not active.");
        }

        resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + resourceId + " not found."));

        if (allocationRepository.existsByResourceIdAndEndTimeIsNull(resourceId)) {
            throw new ResourceInUseException("Cannot create allocation. Resource with id " + resourceId + " is already allocated.");
        }

        Allocation allocation = new Allocation(userId, resourceId);
        return allocationRepository.save(allocation);
    }

    public Allocation endAllocation(String id) {
        if (id == null || id.isBlank()) {
            throw new AllocationException("Allocation ID cannot be blank.");
        }
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));

        if (allocation.getEndTime() != null) {
            throw new AllocationException("Allocation with id " + id + " has already been ended.");
        }

        allocation.setEndTime(LocalDateTime.now());
        return allocationRepository.save(allocation);
    }

    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            throw new AllocationException("Allocation ID cannot be blank.");
        }
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));

        if (allocation.getEndTime() != null) {
            throw new AllocationException("Cannot delete an ended allocation.");
        }
        allocationRepository.deleteById(id);
    }

    public List<Allocation> getCurrentAllocationsForUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new AllocationException("User ID cannot be blank.");
        }
        return allocationRepository.findByUserId(userId).stream()
                .filter(a -> a.getEndTime() == null)
                .collect(Collectors.toList());
    }

    public List<Allocation> getPastAllocationsForUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new AllocationException("User ID cannot be blank.");
        }
        return allocationRepository.findByUserId(userId).stream()
                .filter(a -> a.getEndTime() != null)
                .collect(Collectors.toList());
    }

    public List<Allocation> getCurrentAllocationsForResource(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            throw new AllocationException("Resource ID cannot be blank.");
        }
        return allocationRepository.findByResourceId(resourceId).stream()
                .filter(a -> a.getEndTime() == null)
                .collect(Collectors.toList());
    }

    public List<Allocation> getPastAllocationsForResource(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            throw new AllocationException("Resource ID cannot be blank.");
        }
        return allocationRepository.findByResourceId(resourceId).stream()
                .filter(a -> a.getEndTime() != null)
                .collect(Collectors.toList());
    }
}
