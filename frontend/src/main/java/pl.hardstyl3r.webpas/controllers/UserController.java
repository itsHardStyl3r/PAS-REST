package pl.hardstyl3r.webpas.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.webpas.dto.RegisterRequest;
import pl.hardstyl3r.webpas.dto.UserDTO;
import pl.hardstyl3r.webpas.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        model.addAttribute("pageTitle", "Rejestracja");
        model.addAttribute("activeMenu", "register");
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                                      BindingResult bindingResult, Model model) {
        model.addAttribute("pageTitle", "Rejestracja");
        model.addAttribute("activeMenu", "register");
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registerRequest);
            return "redirect:/users/register?success";
        } catch (Exception e) {
            bindingResult.reject("registrationError", "Wystąpił błąd podczas rejestracji.");
            return "register";
        }
    }

    @GetMapping("/search")
    public String showSearchPage(Model model) {
        model.addAttribute("pageTitle", "Wyszukiwanie użytkowników");
        model.addAttribute("activeMenu", "users_search");
        return "users";
    }

    // Proxy
    @GetMapping("/api/search/{searchTerm}")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> searchUsersApi(@PathVariable String searchTerm) {
        List<UserDTO> users = userService.searchUsers(searchTerm);
        return ResponseEntity.ok(users);
    }
}
