package pl.hardstyl3r.webpas.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.hardstyl3r.webpas.dto.LoginForm;
import pl.hardstyl3r.webpas.dto.TokenResponse;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final String userApiUrl;

    public AuthService(RestTemplate restTemplate,
                       @Value("${rest.api.user-base-url}") String userApiUrl) {
        this.restTemplate = restTemplate;
        this.userApiUrl = userApiUrl;
    }

    @CircuitBreaker(name = "userService")
    @Retry(name = "userService")
    public TokenResponse login(LoginForm loginForm) {
        String url = userApiUrl + "/api/v1/auth/login";
        return restTemplate.postForObject(url, loginForm, TokenResponse.class);
    }
}
