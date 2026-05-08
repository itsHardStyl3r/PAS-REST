package pl.hardstyl3r.rentservice.soap.endpoints;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.hardstyl3r.rentservice.soap.api.CreateAllocationRequest;
import pl.hardstyl3r.rentservice.soap.api.CreateAllocationResponse;
import pl.hardstyl3r.rentservice.soap.api.SoapMapper;
import pl.hardstyl3r.rentservice.services.AllocationService;

@Endpoint
public class AllocationSoapEndpoint {

    private static final String NAMESPACE = "http://p.lodz.pl/pas/soap";

    private final AllocationService allocationService;

    public AllocationSoapEndpoint(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreateAllocationRequest")
    @ResponsePayload
    public CreateAllocationResponse createAllocation(@RequestPayload CreateAllocationRequest request) {
        CreateAllocationResponse response = new CreateAllocationResponse();
        response.setAllocation(SoapMapper.fromAllocation(
                allocationService.createAllocation(request.getUserId(), request.getResourceId())
        ));
        return response;
    }
}
