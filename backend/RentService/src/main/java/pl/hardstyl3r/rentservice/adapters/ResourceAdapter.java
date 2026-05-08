package pl.hardstyl3r.rentservice.adapters;

import org.springframework.stereotype.Component;
import pl.hardstyl3r.rentservice.domain.resource.Resource;
import pl.hardstyl3r.rentservice.ports.driven.ResourcePort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ResourceAdapter implements ResourcePort {

    private final ResourceRepository resourceRepository;

    public ResourceAdapter(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public List<Resource> findAll() {
        return resourceRepository.findAll().stream().map(ResourceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Resource> findById(String id) {
        return resourceRepository.findById(id).map(ResourceMapper::toDomain);
    }

    @Override
    public Resource save(Resource resource) {
        return ResourceMapper.toDomain(resourceRepository.save(ResourceMapper.toEntity(resource)));
    }

    @Override
    public void deleteById(String id) {
        resourceRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return resourceRepository.existsById(id);
    }
}
