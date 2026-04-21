package pl.hardstyl3r.pas.soap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.hardstyl3r.pas.soap.api.CreateAllocationRequest;
import pl.hardstyl3r.pas.soap.api.GetResourcesRequest;
import pl.hardstyl3r.pas.soap.api.GetUserByUsernameRequest;
import pl.hardstyl3r.pas.soap.endpoints.AllocationSoapEndpoint;
import pl.hardstyl3r.pas.soap.endpoints.ResourceSoapEndpoint;
import pl.hardstyl3r.pas.soap.endpoints.UserSoapEndpoint;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.viewports.AllocationViewPort;
import pl.hardstyl3r.pas.v1.viewports.ResourceViewPort;
import pl.hardstyl3r.pas.v1.viewports.UserViewPort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoapEndpointsUnitTest extends BaseMongoIntegrationTest {

    @Mock
    private UserViewPort userViewPort;

    @Mock
    private ResourceViewPort resourceViewPort;

    @Mock
    private AllocationViewPort allocationViewPort;

    private UserSoapEndpoint userSoapEndpoint;
    private ResourceSoapEndpoint resourceSoapEndpoint;
    private AllocationSoapEndpoint allocationSoapEndpoint;

    @BeforeEach
    void setup() {
        userSoapEndpoint = new UserSoapEndpoint(userViewPort);
        resourceSoapEndpoint = new ResourceSoapEndpoint(resourceViewPort);
        allocationSoapEndpoint = new AllocationSoapEndpoint(allocationViewPort);
    }

    @Test
    void shouldReturnUserByUsername() {
        User user = new User("anna", "encoded", "Anna", true);
        user.setId("u1");
        user.setRole(UserRole.CLIENT);
        when(userViewPort.findUserByUsername("anna")).thenReturn(Optional.of(user));

        GetUserByUsernameRequest request = new GetUserByUsernameRequest();
        request.setUsername("anna");

        var response = userSoapEndpoint.getUserByUsername(request);

        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getId()).isEqualTo("u1");
        assertThat(response.getUser().getUsername()).isEqualTo("anna");
    }

    @Test
    void shouldReturnResourcesList() {
        Book book = new Book("r1", "Book 1", "Desc", "Author", "9788327159779");
        when(resourceViewPort.findAll()).thenReturn(List.of(book));

        var response = resourceSoapEndpoint.getResources(new GetResourcesRequest());

        assertThat(response.getResources()).hasSize(1);
        assertThat(response.getResources().get(0).getId()).isEqualTo("r1");
    }

    @Test
    void shouldCreateAllocation() {
        Allocation allocation = new Allocation("u1", "r1");
        allocation.setId("a1");
        when(allocationViewPort.createAllocation("u1", "r1")).thenReturn(allocation);

        CreateAllocationRequest request = new CreateAllocationRequest();
        request.setUserId("u1");
        request.setResourceId("r1");

        var response = allocationSoapEndpoint.createAllocation(request);

        assertThat(response.getAllocation()).isNotNull();
        assertThat(response.getAllocation().getId()).isEqualTo("a1");
    }
}