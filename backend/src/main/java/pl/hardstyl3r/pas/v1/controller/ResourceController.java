package pl.hardstyl3r.pas.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.dto.EditResourceDTO;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;
import pl.hardstyl3r.pas.v1.services.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<Resource> getAllResources() {
        return resourceService.findAll();
    }

    @GetMapping("/{id}")
    public Resource getResourceById(@PathVariable String id) {
        return resourceService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found."));
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody CreateResourceDTO createResourceDTO) {
        Resource createdResource = resourceService.createResource(createResourceDTO);
        return ResponseEntity.ok(createdResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> editResource(@PathVariable String id, @RequestBody EditResourceDTO editResourceDTO) {
        Resource editedResource = resourceService.updateResource(id, editResourceDTO);
        return ResponseEntity.ok(editedResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable String id) {
        resourceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
