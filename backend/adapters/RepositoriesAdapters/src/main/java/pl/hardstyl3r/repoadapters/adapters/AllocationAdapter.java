package pl.hardstyl3r.repoadapters.adapters;

import org.springframework.stereotype.Component;
import pl.hardstyl3r.appports.AllocationPort;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.repoadapters.mappers.AllocationMapper;
import pl.hardstyl3r.repoadapters.repositories.AllocationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AllocationAdapter implements AllocationPort {

    private final AllocationRepository allocationRepository;

    public AllocationAdapter(AllocationRepository allocationRepository) {
        this.allocationRepository = allocationRepository;
    }

    @Override
    public List<Allocation> findAll() {
        return allocationRepository.findAll().stream()
                .map(AllocationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Allocation> findById(String id) {
        return allocationRepository.findById(id).map(AllocationMapper::toDomain);
    }

    @Override
    public Allocation save(Allocation allocation) {
        return AllocationMapper.toDomain(allocationRepository.save(AllocationMapper.toEntity(allocation)));
    }

    @Override
    public void deleteById(String id) {
        allocationRepository.deleteById(id);
    }

    @Override
    public boolean existsByResourceIdAndEndTimeIsNull(String resourceId) {
        return allocationRepository.existsByResourceIdAndEndTimeIsNull(resourceId);
    }

    @Override
    public List<Allocation> findByUserId(String userId) {
        return allocationRepository.findByUserId(userId).stream()
                .map(AllocationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Allocation> findByResourceId(String resourceId) {
        return allocationRepository.findByResourceId(resourceId).stream()
                .map(AllocationMapper::toDomain)
                .collect(Collectors.toList());
    }
}