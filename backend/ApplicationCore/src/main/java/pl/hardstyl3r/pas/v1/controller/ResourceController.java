package pl.hardstyl3r.pas.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.dto.EditResourceDTO;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.services.ResourceService;
import pl.hardstyl3r.repoadapters.objects.resources.ResourceEnt;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@CrossOrigin(origins = "http://localhost:5173")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<ResourceEnt> getAllResources() {
        return resourceService.findAll();
    }

    @GetMapping("/{id}")
    public ResourceEnt getResourceById(@PathVariable String id) {
        return resourceService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found."));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<ResourceEnt> createResource(@RequestBody CreateResourceDTO createResourceDTO) {
        ResourceEnt createdResource = resourceService.createResource(createResourceDTO);
        return ResponseEntity.ok(createdResource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<ResourceEnt> editResource(@PathVariable String id, @RequestBody EditResourceDTO editResourceDTO) {
        ResourceEnt editedResource = resourceService.updateResource(id, editResourceDTO);
        return ResponseEntity.ok(editedResource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
        resourceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
