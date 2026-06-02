package pl.hardstyl3r.webpas.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pl.hardstyl3r.webpas.security.AuthSession;

@ControllerAdvice
public class GlobalModelAttributes {

    private final AuthSession authSession;

    public GlobalModelAttributes(AuthSession authSession) {
        this.authSession = authSession;
    }

    @ModelAttribute("authSession")
    public AuthSession authSession() {
        return authSession;
    }
}
