package pl.hardstyl3r.pas.soap.client;

import org.springframework.ws.client.core.WebServiceTemplate;
import pl.hardstyl3r.pas.soap.api.CreateAllocationRequest;
import pl.hardstyl3r.pas.soap.api.CreateAllocationResponse;
import pl.hardstyl3r.pas.soap.api.GetResourcesRequest;
import pl.hardstyl3r.pas.soap.api.GetResourcesResponse;
import pl.hardstyl3r.pas.soap.api.GetUserByUsernameRequest;
import pl.hardstyl3r.pas.soap.api.GetUserByUsernameResponse;

/**
 * SOAP client wygenerowany na podstawie WSDL.
 * Komunikuje się z serwisem SOAP na adresie http://localhost:8080/ws
 */
public class SoapClient {

    private static final String URI = "http://localhost:8080/ws";

    private final WebServiceTemplate webServiceTemplate;

    public SoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public GetUserByUsernameResponse getUserByUsername(String username) {
        GetUserByUsernameRequest request = new GetUserByUsernameRequest();
        request.setUsername(username);
        return (GetUserByUsernameResponse) webServiceTemplate.marshalSendAndReceive(URI, request);
    }

    public GetResourcesResponse getResources() {
        GetResourcesRequest request = new GetResourcesRequest();
        return (GetResourcesResponse) webServiceTemplate.marshalSendAndReceive(URI, request);
    }

    public CreateAllocationResponse createAllocation(String userId, String resourceId) {
        CreateAllocationRequest request = new CreateAllocationRequest();
        request.setUserId(userId);
        request.setResourceId(resourceId);
        return (CreateAllocationResponse) webServiceTemplate.marshalSendAndReceive(URI, request);
    }
}

