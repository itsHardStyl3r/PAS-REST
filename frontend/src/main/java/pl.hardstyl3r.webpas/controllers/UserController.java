package pl.hardstyl3r.webpas.controllers;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.hardstyl3r.webpas.dto.RegisterRequest;
import pl.hardstyl3r.webpas.services.UserService;

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
}
