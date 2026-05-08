package pl.hardstyl3r.rentservice.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.rentservice.domain.exception.ResourceInUseException;
import pl.hardstyl3r.rentservice.domain.exception.ResourceNotFoundException;
import pl.hardstyl3r.rentservice.domain.exception.ResourceValidationException;
import pl.hardstyl3r.rentservice.domain.resource.Book;
import pl.hardstyl3r.rentservice.domain.resource.Newspaper;
import pl.hardstyl3r.rentservice.domain.resource.Periodical;
import pl.hardstyl3r.rentservice.domain.resource.Resource;
import pl.hardstyl3r.rentservice.ports.driven.AllocationPort;
import pl.hardstyl3r.rentservice.ports.driven.ResourcePort;
import pl.hardstyl3r.rentservice.ports.driving.CreateResourceCommand;
import pl.hardstyl3r.rentservice.ports.driving.EditResourceCommand;
import pl.hardstyl3r.rentservice.ports.driving.ResourceViewPort;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService implements ResourceViewPort {
    private final ResourcePort resourcePort;
    private final AllocationPort allocationPort;

    public ResourceService(ResourcePort resourcePort, AllocationPort allocationPort) {
        this.resourcePort = resourcePort;
        this.allocationPort = allocationPort;
    }

    @Override
    public List<Resource> findAll() {
        return resourcePort.findAll();
    }

    @Override
    public Optional<Resource> findById(String id) {
        return resourcePort.findById(id);
    }

    private boolean isValidIsbn(String isbn) {
        if (isbn == null) return false;
        String cleaned = isbn.replace("-", "");
        return cleaned.matches("^(?:978|979)?\\d{10}$") || cleaned.matches("^\\d{9}[\\dXx]$");
    }

    private boolean isValidDate(String date) {
        if (date == null) return false;
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    public Resource createResource(CreateResourceCommand command) {
        validateDto(command.name(), command.description());

        Resource resource;
        switch (command.type().toLowerCase()) {
            case "book":
                validateBook(command.author(), command.isbn());
                resource = new Book(null, command.name(), command.description(), command.author(), command.isbn());
                break;
            case "periodical":
                if (command.issueNumber() == null || command.issueNumber() <= 0) {
                    throw new ResourceValidationException("Valid issue number is required for a periodical.");
                }
                resource = new Periodical(null, command.name(), command.description(), command.issueNumber());
                break;
            case "newspaper":
                if (!isValidDate(command.releaseDate())) {
                    throw new ResourceValidationException("Valid release date is required for a newspaper.");
                }
                resource = new Newspaper(null, command.name(), command.description(), command.releaseDate());
                break;
            default:
                throw new ResourceValidationException("Invalid resource type: " + command.type());
        }
        return resourcePort.save(resource);
    }

    @Override
    public void deleteById(String id) {
        if (!resourcePort.existsById(id)) {
            throw new ResourceNotFoundException("Resource with id " + id + " not found.");
        }
        if (!allocationPort.findByResourceId(id).isEmpty()) {
            throw new ResourceInUseException("Cannot delete resource with id " + id + " because it is in use.");
        }
        resourcePort.deleteById(id);
    }

    @Override
    public Resource updateResource(String id, EditResourceCommand command) {
        Resource resource = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found."));

        validateDto(command.name(), command.description());
        resource.setName(command.name());
        resource.setDescription(command.description());

        if (resource instanceof Book book) {
            validateBook(command.author(), command.isbn());
            book.setAuthor(command.author());
            book.setIsbn(command.isbn());
        } else if (resource instanceof Periodical periodical) {
            periodical.setIssueNumber(command.issueNumber());
        } else if (resource instanceof Newspaper newspaper) {
            if (!isValidDate(command.releaseDate())) {
                throw new ResourceValidationException("Invalid date format for releaseDate. Expected format is YYYY-MM-DD.");
            }
            newspaper.setReleaseDate(command.releaseDate());
        }
        return resourcePort.save(resource);
    }

    private void validateDto(String name, String desc) {
        if (name == null || name.isBlank() || name.length() < 3) throw new ResourceValidationException("Invalid name.");
        if (desc == null || desc.isBlank() || desc.length() < 3) throw new ResourceValidationException("Invalid description.");
    }

    private void validateBook(String author, String isbn) {
        if (author == null || author.length() < 3) throw new ResourceValidationException("Invalid author.");
        if (!isValidIsbn(isbn)) throw new ResourceValidationException("Invalid ISBN.");
    }
}
