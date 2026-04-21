package pl.hardstyl3r.pas.soap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ws.test.server.MockWebServiceClient;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.viewports.AllocationViewPort;
import pl.hardstyl3r.pas.v1.viewports.ResourceViewPort;
import pl.hardstyl3r.pas.v1.viewports.UserViewPort;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SpringBootTest(classes = SpringSoapApplication.class)
class SoapEndpointsIntegrationTest {

    private static final String NS = "http://p.lodz.pl/pas/soap";

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoBean
    private UserViewPort userViewPort;

    @MockitoBean
    private ResourceViewPort resourceViewPort;

    @MockitoBean
    private AllocationViewPort allocationViewPort;

    private MockWebServiceClient client;

    @BeforeEach
    void setup() {
        client = MockWebServiceClient.createClient(applicationContext);
    }

    @Test
    void shouldReturnUserByUsername() {
        User user = new User("anna", "encoded", "Anna", true);
        user.setId("u1");
        user.setRole(UserRole.CLIENT);
        when(userViewPort.findUserByUsername("anna")).thenReturn(Optional.of(user));

        client.sendRequest(withPayload(new StreamSource(new StringReader("""
                <tns:GetUserByUsernameRequest xmlns:tns="http://p.lodz.pl/pas/soap">
                    <tns:username>anna</tns:username>
                </tns:GetUserByUsernameRequest>
                """))))
                .andExpect(noFault())
                .andExpect(xpath("/tns:GetUserByUsernameResponse/tns:user/tns:id", ns()).evaluatesTo("u1"))
                .andExpect(xpath("/tns:GetUserByUsernameResponse/tns:user/tns:username", ns()).evaluatesTo("anna"));
    }

    @Test
    void shouldReturnResourcesList() {
        Book book = new Book("r1", "Book 1", "Desc", "Author", "9788327159779");
        when(resourceViewPort.findAll()).thenReturn(List.of(book));

        client.sendRequest(withPayload(new StreamSource(new StringReader("""
                <tns:GetResourcesRequest xmlns:tns="http://p.lodz.pl/pas/soap"/>
                """))))
                .andExpect(noFault())
                .andExpect(xpath("/*[local-name()='GetResourcesResponse']/*[local-name()='resource']/*[local-name()='id']", ns()).evaluatesTo("r1"));
    }

    @Test
    void shouldCreateAllocation() {
        Allocation allocation = new Allocation("u1", "r1");
        allocation.setId("a1");
        when(allocationViewPort.createAllocation(any(), any())).thenReturn(allocation);

        client.sendRequest(withPayload(new StreamSource(new StringReader("""
                <tns:CreateAllocationRequest xmlns:tns="http://p.lodz.pl/pas/soap">
                    <tns:userId>u1</tns:userId>
                    <tns:resourceId>r1</tns:resourceId>
                </tns:CreateAllocationRequest>
                """))))
                .andExpect(noFault())
                .andExpect(xpath("/tns:CreateAllocationResponse/tns:allocation/tns:id", ns()).evaluatesTo("a1"));
    }

    private Map<String, String> ns() {
        return Map.of("tns", NS);
    }
}