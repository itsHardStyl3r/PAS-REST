package pl.hardstyl3r.webpas.services;

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

import java.util.List;

@Service
public class AllocationService {

    private final RestTemplate restTemplate;
    private final String restApiUrl;
    private final String apiToken;

    public AllocationService(RestTemplate restTemplate,
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

    public List<AllocationDTO> getAllAllocations() {
        String url = restApiUrl + "/api/v1/allocations";
        ResponseEntity<List<AllocationDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public void createAllocation(AllocationRequest allocationRequest) {
        String url = restApiUrl + "/api/v1/allocations";
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<AllocationRequest> entity = new HttpEntity<>(allocationRequest, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public void endAllocation(String allocationId) {
        String url = restApiUrl + "/api/v1/allocations/" + allocationId + "/end";
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
