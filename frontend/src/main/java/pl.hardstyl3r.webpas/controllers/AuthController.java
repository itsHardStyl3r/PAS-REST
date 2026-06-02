package pl.hardstyl3r.webpas.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.LocaleResolver;
import pl.hardstyl3r.webpas.dto.LoginForm;
import pl.hardstyl3r.webpas.dto.TokenResponse;
import pl.hardstyl3r.webpas.security.AuthSession;
import pl.hardstyl3r.webpas.services.AuthService;

@Controller
public class AuthController {

    private final AuthService authService;
    private final AuthSession authSession;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public AuthController(AuthService authService, AuthSession authSession, MessageSource messageSource, LocaleResolver localeResolver) {
        this.authService = authService;
        this.authSession = authSession;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        model.addAttribute("pageTitle", messageSource.getMessage("breadcrumbs.login", null, localeResolver.resolveLocale(request)));
        model.addAttribute("activeMenu", "login");
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("loginForm") LoginForm loginForm, Model model, HttpServletRequest request) {
        try {
            TokenResponse response = authService.login(loginForm);
            authSession.set(response.getToken(), response.getUsername(), response.getRole());
            return "redirect:/allocations";
        } catch (Exception e) {
            model.addAttribute("pageTitle", messageSource.getMessage("breadcrumbs.login", null, localeResolver.resolveLocale(request)));
            model.addAttribute("activeMenu", "login");
            model.addAttribute("loginError", messageSource.getMessage("login.error", null, localeResolver.resolveLocale(request)));
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        authSession.clear();
        return "redirect:/login";
    }
}
