package pl.hardstyl3r.webpas.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import pl.hardstyl3r.webpas.dto.RegisterRequest;
import pl.hardstyl3r.webpas.dto.UserDTO;
import pl.hardstyl3r.webpas.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public UserController(UserService userService, MessageSource messageSource, LocaleResolver localeResolver) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpServletRequest request) {
        model.addAttribute("registerRequest", new RegisterRequest());
        model.addAttribute("pageTitle", messageSource.getMessage("breadcrumbs.register", null, localeResolver.resolveLocale(request)));
        model.addAttribute("activeMenu", "register");
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                                      BindingResult bindingResult, Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", messageSource.getMessage("breadcrumbs.register", null, localeResolver.resolveLocale(request)));
        model.addAttribute("activeMenu", "register");
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(registerRequest);
            return "redirect:/users/register?success";
        } catch (Exception e) {
            bindingResult.reject("registrationError", messageSource.getMessage("register.error", null, localeResolver.resolveLocale(request)));
            return "register";
        }
    }

    @GetMapping("/search")
    public String showSearchPage(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", messageSource.getMessage("users.search.title", null, localeResolver.resolveLocale(request)));
        model.addAttribute("activeMenu", "users_search");
        return "users";
    }

    // Proxy
    @GetMapping("/api/search/{searchTerm}")
    @ResponseBody
    public ResponseEntity<List<UserDTO>> searchUsersApi(@PathVariable String searchTerm) {
        try {
            List<UserDTO> users = userService.searchUsers(searchTerm);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
