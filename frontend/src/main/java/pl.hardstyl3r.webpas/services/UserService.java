package pl.hardstyl3r.webpas.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.hardstyl3r.webpas.dto.RegisterRequest;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final String restApiUrl;

    public UserService(RestTemplate restTemplate, @Value("${rest.api.base-url}") String restApiUrl) {
        this.restTemplate = restTemplate;
        this.restApiUrl = restApiUrl;
    }

    public void registerUser(RegisterRequest registerRequest) {
        String url = restApiUrl + "/api/auth/register";
        restTemplate.postForEntity(url, registerRequest, String.class);
    }
}
