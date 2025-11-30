package pl.hardstyl3r.webpas.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.hardstyl3r.webpas.dto.RegisterRequest;
import pl.hardstyl3r.webpas.dto.UserDTO;

import java.util.List;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final String restApiUrl;
    private final String apiToken;

    public UserService(RestTemplate restTemplate,
                       @Value("${rest.api.base-url}") String restApiUrl,
                       @Value("${rest.api.token}") String apiToken) {
        this.restTemplate = restTemplate;
        this.restApiUrl = restApiUrl;
        this.apiToken = apiToken;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        return headers;
    }

    public void registerUser(RegisterRequest registerRequest) {
        String url = restApiUrl + "/api/auth/register";
        restTemplate.postForEntity(url, registerRequest, String.class);
    }

    public List<UserDTO> searchUsers(String searchTerm) {
        String url = restApiUrl + "/api/v1/user/search/" + searchTerm;
        HttpEntity<Void> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
