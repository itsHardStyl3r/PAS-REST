package pl.hardstyl3r.pas.v1.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.viewports.AllocationViewPort;
import pl.hardstyl3r.pas.v1.viewports.UserViewPort;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AllocationControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private AllocationViewPort allocationViewPort;

    @Mock
    private UserViewPort userViewPort;

    @BeforeEach
    void setup() {
        AllocationController controller = new AllocationController(allocationViewPort, userViewPort);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldReturnAllAllocations() throws Exception {
        Allocation a1 = new Allocation("u1", "r1");
        a1.setId("a1");

        when(allocationViewPort.findAll()).thenReturn(List.of(a1));

        mockMvc.perform(get("/api/v1/allocations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("a1"))
                .andExpect(jsonPath("$[0].userId").value("u1"));
    }

    @Test
    void shouldReturnAllocationById() throws Exception {
        Allocation allocation = new Allocation("u1", "r1");
        allocation.setId("a1");

        when(allocationViewPort.findById("a1")).thenReturn(Optional.of(allocation));

        mockMvc.perform(get("/api/v1/allocations/{id}", "a1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("a1"))
                .andExpect(jsonPath("$.resourceId").value("r1"));
    }
}
