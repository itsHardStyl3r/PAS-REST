package pl.hardstyl3r.pas.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.dto.EditResourceDTO;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;
import pl.hardstyl3r.pas.v1.viewports.CreateResourceCommand;
import pl.hardstyl3r.pas.v1.viewports.EditResourceCommand;
import pl.hardstyl3r.pas.v1.viewports.ResourceViewPort;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@CrossOrigin(origins = "http://localhost:5173")
public class ResourceController {

    private final ResourceViewPort resourceViewPort;

    public ResourceController(ResourceViewPort resourceViewPort) {
        this.resourceViewPort = resourceViewPort;
    }

    @GetMapping
    public List<Resource> getAllResources() {
        return resourceViewPort.findAll();
    }

    @GetMapping("/{id}")
    public Resource getResourceById(@PathVariable String id) {
        return resourceViewPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found."));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Resource> createResource(@RequestBody CreateResourceDTO createResourceDTO) {
        Resource createdResource = resourceViewPort.createResource(new CreateResourceCommand(
                createResourceDTO.type(),
                createResourceDTO.name(),
                createResourceDTO.description(),
                createResourceDTO.author(),
                createResourceDTO.isbn(),
                createResourceDTO.issueNumber(),
                createResourceDTO.releaseDate()
        ));
        return ResponseEntity.ok(createdResource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Resource> editResource(@PathVariable String id, @RequestBody EditResourceDTO editResourceDTO) {
        Resource editedResource = resourceViewPort.updateResource(id, new EditResourceCommand(
                editResourceDTO.name(),
                editResourceDTO.description(),
                editResourceDTO.author(),
                editResourceDTO.isbn(),
                editResourceDTO.issueNumber(),
                editResourceDTO.releaseDate()
        ));
        return ResponseEntity.ok(editedResource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
        resourceViewPort.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
