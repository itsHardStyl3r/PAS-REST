package pl.hardstyl3r.pas.soap.endpoints;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.hardstyl3r.pas.soap.api.GetResourcesRequest;
import pl.hardstyl3r.pas.soap.api.GetResourcesResponse;
import pl.hardstyl3r.pas.soap.api.SoapMapper;
import pl.hardstyl3r.pas.v1.viewports.ResourceViewPort;

import java.util.stream.Collectors;

@Endpoint
public class ResourceSoapEndpoint {

    private static final String NAMESPACE = "http://p.lodz.pl/pas/soap";

    private final ResourceViewPort resourceViewPort;

    public ResourceSoapEndpoint(ResourceViewPort resourceViewPort) {
        this.resourceViewPort = resourceViewPort;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetResourcesRequest")
    @ResponsePayload
    public GetResourcesResponse getResources(@RequestPayload GetResourcesRequest request) {
        GetResourcesResponse response = new GetResourcesResponse();
        response.setResources(resourceViewPort.findAll().stream()
                .map(SoapMapper::fromResource)
                .collect(Collectors.toList()));
        return response;
    }
}

