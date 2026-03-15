package pl.hardstyl3r.repoadapters.adapters;

import org.springframework.stereotype.Component;
import pl.hardstyl3r.pas.appports.ResourcePort;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;
import pl.hardstyl3r.repoadapters.mappers.ResourceMapper;
import pl.hardstyl3r.repoadapters.repositories.ResourceRepository;

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
        return resourceRepository.findAll().stream()
                .map(ResourceMapper::toDomain)
                .collect(Collectors.toList());
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