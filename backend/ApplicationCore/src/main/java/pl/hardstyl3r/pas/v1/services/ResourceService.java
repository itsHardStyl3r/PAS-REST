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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

    private boolean isValidIsbn(String isbn) {
        if (isbn == null) {
            return false;
        }
        String cleanedIsbn = isbn.replace("-", "");
        return cleanedIsbn.matches("^(?:978|979)?\\d{10}$") || cleanedIsbn.matches("^\\d{9}[\\dXx]$");
    }

    private boolean isValidDate(String date) {
        if (date == null) {
            return false;
        }
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public Resource createResource(CreateResourceDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new ResourceValidationException("Resource name cannot be blank.");
        }
        if (dto.name().length() < 3 || dto.name().length() > 100) {
            throw new ResourceValidationException("Resource name must be between 3 and 100 characters.");
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new ResourceValidationException("Resource name cannot be blank.");
        }
        if (dto.description().length() < 3 || dto.description().length() > 255) {
            throw new ResourceValidationException("Resource description must be between 3 and 200 characters.");
        }

        Resource resource;
        switch (dto.type().toLowerCase()) {
            case "book":
                if (dto.author() == null || dto.author().isBlank()) {
                    throw new ResourceValidationException("Author is required for a book.");
                }
                if (dto.author().length() < 3 || dto.author().length() > 50) {
                    throw new ResourceValidationException("Author name must be between 3 and 50 characters.");
                }
                if (dto.isbn() == null || dto.isbn().isBlank()) {
                    throw new ResourceValidationException("ISBN is required for a book.");
                }
                if (!isValidIsbn(dto.isbn())) {
                    throw new ResourceValidationException("Invalid ISBN format.");
                }
                resource = new Book(dto.name(), dto.description(), dto.author(), dto.isbn());
                break;
            case "periodical":
                if (dto.issueNumber() == null) {
                    throw new ResourceValidationException("Issue number is required for a periodical.");
                }
                if (dto.issueNumber() <= 0) {
                    throw new ResourceValidationException("Issue number must be a positive number.");
                }
                resource = new Periodical(dto.name(), dto.description(), dto.issueNumber());
                break;
            case "newspaper":
                if (dto.releaseDate() == null) {
                    throw new ResourceValidationException("Release date is required for a newspaper.");
                }
                if (!isValidDate(dto.releaseDate())) {
                    throw new ResourceValidationException("Invalid date format for releaseDate. Expected format is YYYY-MM-DD.");
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
        if (dto.name().length() < 3 || dto.name().length() > 100) {
            throw new ResourceValidationException("Resource name must be between 3 and 100 characters.");
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new ResourceValidationException("Resource name cannot be blank.");
        }
        if (dto.description().length() < 3 || dto.description().length() > 255) {
            throw new ResourceValidationException("Resource description must be between 3 and 200 characters.");
        }
        resource.setName(dto.name());
        resource.setDescription(dto.description());

        if (resource instanceof Book book) {
            if (dto.author() == null || dto.author().isBlank()) {
                throw new ResourceValidationException("Author is required for a book.");
            }
            if (dto.author().length() < 3 || dto.author().length() > 50) {
                throw new ResourceValidationException("Author name must be between 3 and 50 characters.");
            }
            if (dto.isbn() == null || dto.isbn().isBlank()) {
                throw new ResourceValidationException("ISBN is required for a book.");
            }
            if (!isValidIsbn(dto.isbn())) {
                throw new ResourceValidationException("Invalid ISBN format.");
            }
            book.setAuthor(dto.author());
            book.setIsbn(dto.isbn());
        } else if (resource instanceof Periodical periodical) {
            if (dto.issueNumber() == null) {
                throw new ResourceValidationException("Issue number is required for a periodical.");
            }
            if (dto.issueNumber() <= 0) {
                throw new ResourceValidationException("Issue number must be a positive number.");
            }
            periodical.setIssueNumber(dto.issueNumber());
        } else if (resource instanceof Newspaper newspaper) {
            if (dto.releaseDate() == null) {
                throw new ResourceValidationException("Release date is required for a newspaper.");
            }
            if (!isValidDate(dto.releaseDate())) {
                throw new ResourceValidationException("Invalid date format for releaseDate. Expected format is YYYY-MM-DD.");
            }
            newspaper.setReleaseDate(dto.releaseDate());
        }
        return resourceRepository.save(resource);
    }
}
