package pl.hardstyl3r.pas.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.hardstyl3r.pas.v1.dto.CreateResourceDTO;
import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.viewports.ResourceViewPort;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResourceControllerUnitTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ResourceViewPort resourceViewPort;

    @BeforeEach
    void setup() {
        ResourceController controller = new ResourceController(resourceViewPort);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldReturnAllResources() throws Exception {
        Book b1 = new Book("r1", "Book 1", "Desc", "Author", "9788327159779");
        Book b2 = new Book("r2", "Book 2", "Desc", "Author", "9788327165596");

        when(resourceViewPort.findAll()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/v1/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("r1"))
                .andExpect(jsonPath("$[1].id").value("r2"));
    }

    @Test
    void shouldReturnResourceById() throws Exception {
        Book resource = new Book("r1", "Book 1", "Desc", "Author", "9788327159779");
        when(resourceViewPort.findById("r1")).thenReturn(Optional.of(resource));

        mockMvc.perform(get("/api/v1/resources/{id}", "r1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Book 1"));
    }

    @Test
    void shouldCreateResource() throws Exception {
        CreateResourceDTO dto = new CreateResourceDTO("book", "Book 1", "Desc", "Author", "9788327159779", null, null);
        Book created = new Book("r1", "Book 1", "Desc", "Author", "9788327159779");

        when(resourceViewPort.createResource(any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r1"));
    }
}
