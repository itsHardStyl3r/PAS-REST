package pl.hardstyl3r.webpas.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.hardstyl3r.webpas.dto.AllocationDTO;
import pl.hardstyl3r.webpas.dto.AllocationRequest;
import pl.hardstyl3r.webpas.security.AuthSession;

import java.util.List;

@Service
public class AllocationService {

    private final RestTemplate restTemplate;
    private final String rentApiUrl;
    private final AuthSession authSession;

    public AllocationService(RestTemplate restTemplate,
                             @Value("${rest.api.rent-base-url}") String rentApiUrl,
                             AuthSession authSession) {
        this.restTemplate = restTemplate;
        this.rentApiUrl = rentApiUrl;
        this.authSession = authSession;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (authSession.isAuthenticated()) {
            headers.setBearerAuth(authSession.getToken());
        }
        return headers;
    }

    @CircuitBreaker(name = "rentService", fallbackMethod = "getAllAllocationsFallback")
    @Retry(name = "rentService")
    public List<AllocationDTO> getAllAllocations() {
        String url = rentApiUrl + "/api/v1/allocations";
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
        ResponseEntity<List<AllocationDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @CircuitBreaker(name = "rentService")
    @Retry(name = "rentService")
    public void createAllocation(AllocationRequest allocationRequest) {
        String url = rentApiUrl + "/api/v1/allocations";
        HttpEntity<AllocationRequest> entity = new HttpEntity<>(allocationRequest, authHeaders());
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @CircuitBreaker(name = "rentService")
    @Retry(name = "rentService")
    public void endAllocation(String allocationId) {
        String url = rentApiUrl + "/api/v1/allocations/" + allocationId + "/end";
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public List<AllocationDTO> getAllAllocationsFallback(Throwable t) {
        return List.of();
    }
}
