package pl.hardstyl3r.pas.soap.endpoints;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.hardstyl3r.pas.soap.api.CreateAllocationRequest;
import pl.hardstyl3r.pas.soap.api.CreateAllocationResponse;
import pl.hardstyl3r.pas.soap.api.SoapMapper;
import pl.hardstyl3r.pas.v1.viewports.AllocationViewPort;

@Endpoint
public class AllocationSoapEndpoint {

    private static final String NAMESPACE = "http://p.lodz.pl/pas/soap";

    private final AllocationViewPort allocationViewPort;

    public AllocationSoapEndpoint(AllocationViewPort allocationViewPort) {
        this.allocationViewPort = allocationViewPort;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreateAllocationRequest")
    @ResponsePayload
    public CreateAllocationResponse createAllocation(@RequestPayload CreateAllocationRequest request) {
        CreateAllocationResponse response = new CreateAllocationResponse();
        response.setAllocation(SoapMapper.fromAllocation(
                allocationViewPort.createAllocation(request.getUserId(), request.getResourceId())
        ));
        return response;
    }
}

