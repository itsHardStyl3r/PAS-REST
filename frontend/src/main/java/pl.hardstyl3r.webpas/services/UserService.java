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
import pl.hardstyl3r.webpas.security.AuthSession;

import java.util.List;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final String userApiUrl;
    private final AuthSession authSession;

    public UserService(RestTemplate restTemplate,
                       @Value("${rest.api.user-base-url}") String userApiUrl,
                       AuthSession authSession) {
        this.restTemplate = restTemplate;
        this.userApiUrl = userApiUrl;
        this.authSession = authSession;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (authSession.isAuthenticated()) {
            headers.setBearerAuth(authSession.getToken());
        }
        return headers;
    }

    public void registerUser(RegisterRequest registerRequest) {
        String url = userApiUrl + "/api/v1/auth/register";
        restTemplate.postForEntity(url, registerRequest, String.class);
    }

    public List<UserDTO> searchUsers(String searchTerm) {
        String url = userApiUrl + "/api/v1/user/search/" + searchTerm;
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
