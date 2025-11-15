package pl.hardstyl3r.pas.v1.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.dto.EditResourceDTO;
import pl.hardstyl3r.pas.v1.exceptions.ResourceInUseException;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.ResourceValidationException;
import pl.hardstyl3r.pas.v1.objects.resources.*;
import pl.hardstyl3r.pas.v1.repositories.AllocationRepository;
import pl.hardstyl3r.pas.v1.repositories.ResourceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final AllocationRepository allocationRepository;

    public ResourceService(ResourceRepository resourceRepository, AllocationRepository allocationRepository) {
        this.resourceRepository = resourceRepository;
        this.allocationRepository = allocationRepository;
    }

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public Optional<Resource> findById(String id) {
        return resourceRepository.findById(id);
    }

    public Resource createResource(CreateResourceDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new ResourceValidationException("Resource name cannot be blank.");
        }

        Resource resource;
        switch (dto.type().toLowerCase()) {
            case "book":
                if (dto.author() == null || dto.author().isBlank() || dto.isbn() == null || dto.isbn().isBlank()) {
                    throw new ResourceValidationException("Author and ISBN are required for a book.");
                }
                resource = new Book(dto.name(), dto.description(), dto.author(), dto.isbn());
                break;
            case "periodical":
                if (dto.issueNumber() == null) {
                    throw new ResourceValidationException("Issue number is required for a periodical.");
                }
                resource = new Periodical(dto.name(), dto.description(), dto.issueNumber());
                break;
            case "newspaper":
                if (dto.releaseDate() == null) {
                    throw new ResourceValidationException("Release date is required for a newspaper.");
                }
                resource = new Newspaper(dto.name(), dto.description(), dto.releaseDate());
                break;
            default:
                throw new ResourceValidationException("Invalid resource type: " + dto.type());
        }
        return resourceRepository.save(resource);
    }

    public void deleteById(String id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource with id " + id + " not found.");
        }
        if (!allocationRepository.findByResourceId(id).isEmpty()) {
            throw new ResourceInUseException("Cannot delete resource with id " + id + " because it is associated with an allocation.");
        }
        resourceRepository.deleteById(id);
    }

    public Resource updateResource(String id, EditResourceDTO dto) {
        Resource resource = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found."));

        if (dto.name() == null || dto.name().isBlank()) {
            throw new ResourceValidationException("Resource name cannot be blank.");
        }
        resource.setName(dto.name());
        resource.setDescription(dto.description());

        if (resource instanceof Book book) {
            if (dto.author() == null || dto.author().isBlank() || dto.isbn() == null || dto.isbn().isBlank()) {
                throw new ResourceValidationException("Author and ISBN are required for a book.");
            }
            book.setAuthor(dto.author());
            book.setIsbn(dto.isbn());
        } else if (resource instanceof Periodical periodical) {
            if (dto.issueNumber() == null) {
                throw new ResourceValidationException("Issue number is required for a periodical.");
            }
            periodical.setIssueNumber(dto.issueNumber());
        } else if (resource instanceof Newspaper newspaper) {
            if (dto.releaseDate() == null) {
                throw new ResourceValidationException("Release date is required for a newspaper.");
            }
            newspaper.setReleaseDate(dto.releaseDate());
        }
        return resourceRepository.save(resource);
    }
}
