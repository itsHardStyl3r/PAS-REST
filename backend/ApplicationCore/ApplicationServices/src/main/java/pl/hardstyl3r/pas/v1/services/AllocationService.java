package pl.hardstyl3r.pas.v1.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.appports.AllocationPort;
import pl.hardstyl3r.pas.appports.ResourcePort;
import pl.hardstyl3r.pas.appports.UserPort;
import pl.hardstyl3r.pas.v1.exceptions.*;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.viewports.AllocationViewPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AllocationService implements AllocationViewPort {

    private final AllocationPort allocationPort;
    private final UserPort userPort;
    private final ResourcePort resourcePort;

    public AllocationService(AllocationPort allocationPort, UserPort userPort, ResourcePort resourcePort) {
        this.allocationPort = allocationPort;
        this.userPort = userPort;
        this.resourcePort = resourcePort;
    }

    @Override
    public List<Allocation> findAll() {
        return allocationPort.findAll();
    }

    @Override
    public Optional<Allocation> findById(String id) {
        return allocationPort.findById(id);
    }

    @Override
    public Allocation createAllocation(String userId, String resourceId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        if (!user.isActive()) {
            throw new UserNotActiveException("User with id " + userId + " is not active.");
        }

        resourcePort.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + resourceId + " not found."));

        if (allocationPort.existsByResourceIdAndEndTimeIsNull(resourceId)) {
            throw new ResourceInUseException("Resource with id " + resourceId + " is already allocated.");
        }

        Allocation allocation = new Allocation(userId, resourceId);
        return allocationPort.save(allocation);
    }

    @Override
    public Allocation endAllocation(String id) {
        Allocation allocation = allocationPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));

        if (allocation.getEndTime() != null) {
            throw new AllocationException("Allocation with id " + id + " has already been ended.");
        }

        allocation.setEndTime(LocalDateTime.now());
        return allocationPort.save(allocation);
    }

    @Override
    public void deleteById(String id) {
        Allocation allocation = allocationPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation with id " + id + " not found."));

        if (allocation.getEndTime() != null) {
            throw new AllocationException("Cannot delete an ended allocation.");
        }
        allocationPort.deleteById(id);
    }

    @Override
    public List<Allocation> getCurrentAllocationsForUser(String userId) {
        return allocationPort.findByUserId(userId).stream()
                .filter(a -> a.getEndTime() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Allocation> getPastAllocationsForUser(String userId) {
        return allocationPort.findByUserId(userId).stream()
                .filter(a -> a.getEndTime() != null)
                .collect(Collectors.toList());
    }
}