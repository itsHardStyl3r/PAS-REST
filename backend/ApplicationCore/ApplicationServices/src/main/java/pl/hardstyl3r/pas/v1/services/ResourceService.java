package pl.hardstyl3r.pas.v1.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.appports.AllocationPort;
import pl.hardstyl3r.pas.appports.ResourcePort;
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.dto.EditResourceDTO;
import pl.hardstyl3r.pas.v1.exceptions.ResourceInUseException;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.ResourceValidationException;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.objects.resources.Newspaper;
import pl.hardstyl3r.pas.v1.objects.resources.Periodical;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private final ResourcePort resourcePort;
    private final AllocationPort allocationPort;

    public ResourceService(ResourcePort resourcePort, AllocationPort allocationPort) {
        this.resourcePort = resourcePort;
        this.allocationPort = allocationPort;
    }

    public List<Resource> findAll() {
        return resourcePort.findAll();
    }

    public Optional<Resource> findById(String id) {
        return resourcePort.findById(id);
    }

    private boolean isValidIsbn(String isbn) {
        if (isbn == null) return false;
        String cleanedIsbn = isbn.replace("-", "");
        return cleanedIsbn.matches("^(?:978|979)?\\d{10}$") || cleanedIsbn.matches("^\\d{9}[\\dXx]$");
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

    public Resource createResource(CreateResourceDTO dto) {
        validateDto(dto.name(), dto.description());

        Resource resource;
        switch (dto.type().toLowerCase()) {
            case "book":
                validateBook(dto.author(), dto.isbn());
                resource = new Book(null, dto.name(), dto.description(), dto.author(), dto.isbn());
                break;
            case "periodical":
                if (dto.issueNumber() == null || dto.issueNumber() <= 0) {
                    throw new ResourceValidationException("Valid issue number is required for a periodical.");
                }
                resource = new Periodical(null, dto.name(), dto.description(), dto.issueNumber());
                break;
            case "newspaper":
                if (dto.releaseDate() == null || !isValidDate(dto.releaseDate())) {
                    throw new ResourceValidationException("Valid release date is required for a newspaper.");
                }
                resource = new Newspaper(null, dto.name(), dto.description(), dto.releaseDate());
                break;
            default:
                throw new ResourceValidationException("Invalid resource type: " + dto.type());
        }
        return resourcePort.save(resource);
    }

    public void deleteById(String id) {
        if (!resourcePort.existsById(id)) {
            throw new ResourceNotFoundException("Resource with id " + id + " not found.");
        }
        if (!allocationPort.findByResourceId(id).isEmpty()) {
            throw new ResourceInUseException("Cannot delete resource with id " + id + " because it is in use.");
        }
        resourcePort.deleteById(id);
    }

    public Resource updateResource(String id, EditResourceDTO dto) {
        Resource resource = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found."));

        validateDto(dto.name(), dto.description());
        resource.setName(dto.name());
        resource.setDescription(dto.description());

        if (resource instanceof Book book) {
            validateBook(dto.author(), dto.isbn());
            book.setAuthor(dto.author());
            book.setIsbn(dto.isbn());
        } else if (resource instanceof Periodical periodical) {
            periodical.setIssueNumber(dto.issueNumber());
        } else if (resource instanceof Newspaper newspaper) {
            if (!isValidDate(dto.releaseDate())) {
                throw new ResourceValidationException("Invalid date format for releaseDate. Expected format is YYYY-MM-DD.");
            }
            newspaper.setReleaseDate(dto.releaseDate());
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