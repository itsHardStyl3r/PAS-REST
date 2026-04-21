package pl.hardstyl3r.pas.soap.endpoints;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.hardstyl3r.pas.soap.api.GetUserByUsernameRequest;
import pl.hardstyl3r.pas.soap.api.GetUserByUsernameResponse;
import pl.hardstyl3r.pas.soap.api.SoapMapper;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.viewports.UserViewPort;

@Endpoint
public class UserSoapEndpoint {

    private static final String NAMESPACE = "http://p.lodz.pl/pas/soap";

    private final UserViewPort userViewPort;

    public UserSoapEndpoint(UserViewPort userViewPort) {
        this.userViewPort = userViewPort;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "GetUserByUsernameRequest")
    @ResponsePayload
    public GetUserByUsernameResponse getUserByUsername(@RequestPayload GetUserByUsernameRequest request) {
        GetUserByUsernameResponse response = new GetUserByUsernameResponse();
        response.setUser(userViewPort.findUserByUsername(request.getUsername())
                .map(SoapMapper::fromUser)
                .orElseThrow(() -> new UserNotFoundException("User with username " + request.getUsername() + " not found")));
        return response;
    }
}

