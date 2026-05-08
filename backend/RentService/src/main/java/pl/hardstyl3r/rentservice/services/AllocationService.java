package pl.hardstyl3r.rentservice.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.rentservice.domain.Allocation;
import pl.hardstyl3r.rentservice.domain.Client;
import pl.hardstyl3r.rentservice.domain.exception.AllocationException;
import pl.hardstyl3r.rentservice.domain.exception.ResourceInUseException;
import pl.hardstyl3r.rentservice.domain.exception.ResourceNotFoundException;
import pl.hardstyl3r.rentservice.domain.exception.UserNotFoundException;
import pl.hardstyl3r.rentservice.ports.driven.AllocationPort;
import pl.hardstyl3r.rentservice.ports.driven.ClientPort;
import pl.hardstyl3r.rentservice.ports.driven.ResourcePort;
import pl.hardstyl3r.rentservice.ports.driving.AllocationViewPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AllocationService implements AllocationViewPort {

    private final AllocationPort allocationPort;
    private final ClientPort clientPort;
    private final ResourcePort resourcePort;

    public AllocationService(AllocationPort allocationPort, ClientPort clientPort, ResourcePort resourcePort) {
        this.allocationPort = allocationPort;
        this.clientPort = clientPort;
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
        Client client = clientPort.findById(userId)
                .filter(Client::isActive)
                .orElseThrow(() -> new UserNotFoundException("Client with id " + userId + " not found or inactive."));

        resourcePort.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + resourceId + " not found."));

        if (allocationPort.existsByResourceIdAndEndTimeIsNull(resourceId)) {
            throw new ResourceInUseException("Resource with id " + resourceId + " is already allocated.");
        }

        Allocation allocation = new Allocation(client.getId(), resourceId);
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
