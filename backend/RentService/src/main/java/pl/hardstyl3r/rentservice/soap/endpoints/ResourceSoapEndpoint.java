package pl.hardstyl3r.rentservice.soap.endpoints;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.hardstyl3r.rentservice.soap.api.GetResourcesRequest;
import pl.hardstyl3r.rentservice.soap.api.GetResourcesResponse;
import pl.hardstyl3r.rentservice.soap.api.SoapMapper;
import pl.hardstyl3r.rentservice.services.ResourceService;

import java.util.stream.Collectors;

@Endpoint
public class ResourceSoapEndpoint {

    private static final String NAMESPACE = "http://p.lodz.pl/pas/soap";

    private final ResourceService resourceService;

    public ResourceSoapEndpoint(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetResourcesRequest")
    @ResponsePayload
    public GetResourcesResponse getResources(@RequestPayload GetResourcesRequest request) {
        GetResourcesResponse response = new GetResourcesResponse();
        response.setResources(resourceService.findAll().stream()
                .map(SoapMapper::fromResource)
                .collect(Collectors.toList()));
        return response;
    }
}
