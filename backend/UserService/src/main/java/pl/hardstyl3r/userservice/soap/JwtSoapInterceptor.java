package pl.hardstyl3r.userservice.soap;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import pl.hardstyl3r.userservice.security.JwtUtil;

import java.util.Iterator;

public class JwtSoapInterceptor implements EndpointInterceptor {

    private static final String AUTH_HEADER = "authToken";

    private final JwtUtil jwtUtil;

    public JwtSoapInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) {
        if (!(messageContext.getRequest() instanceof SoapMessage soapMessage)) {
            throw new SecurityException("Żądanie nie jest wiadomością SOAP.");
        }
        String token = extractToken(soapMessage.getSoapHeader());
        if (token == null || !isValid(token)) {
            throw new SecurityException("Brak lub niepoprawny token JWT w nagłówku SOAP.");
        }
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) {
    }

    private String extractToken(SoapHeader header) {
        if (header == null) {
            return null;
        }
        Iterator<SoapHeaderElement> elements = header.examineAllHeaderElements();
        while (elements.hasNext()) {
            SoapHeaderElement element = elements.next();
            if (AUTH_HEADER.equals(element.getName().getLocalPart())) {
                return element.getText();
            }
        }
        return null;
    }

    private boolean isValid(String token) {
        try {
            jwtUtil.extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
