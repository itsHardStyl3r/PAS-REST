package pl.hardstyl3r.webpas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.hardstyl3r.webpas.dto.AllocationRequest;
import pl.hardstyl3r.webpas.services.AllocationService;

@Controller
@RequestMapping("/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping
    public String listAllocations(Model model) {
        if (!model.containsAttribute("allocationRequest")) {
            model.addAttribute("allocationRequest", new AllocationRequest());
        }
        model.addAttribute("allocations", allocationService.getAllAllocations());
        model.addAttribute("pageTitle", "Alokacje");
        model.addAttribute("activeMenu", "allocations");
        return "allocations";
    }

    @PostMapping
    public String createAllocation(@ModelAttribute AllocationRequest allocationRequest,
                                   RedirectAttributes redirectAttributes) {
        try {
            allocationService.createAllocation(allocationRequest);
            redirectAttributes.addFlashAttribute("successMessage", "allocations.success.created");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "allocations.error.creating");
            redirectAttributes.addFlashAttribute("errorDetails", e.getMessage());
        }
        return "redirect:/allocations";
    }

    @PostMapping("/{id}/end")
    public String endAllocation(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            allocationService.endAllocation(id);
            redirectAttributes.addFlashAttribute("successMessage", "allocations.success.ended");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "allocations.error.ending");
            redirectAttributes.addFlashAttribute("errorDetails", e.getMessage());
        }
        return "redirect:/allocations";
    }
}
