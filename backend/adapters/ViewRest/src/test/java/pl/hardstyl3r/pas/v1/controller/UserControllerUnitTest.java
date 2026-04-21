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
import pl.hardstyl3r.pas.v1.dto.EditUserDTO;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.security.JwtUtil;
import pl.hardstyl3r.pas.v1.viewports.UserViewPort;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserViewPort userViewPort;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        UserController controller = new UserController(userViewPort, jwtUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User user = new User("anna", "encoded", "Anna", true);
        user.setId("u1");
        user.setRole(UserRole.CLIENT);

        when(userViewPort.findUserById("u1")).thenReturn(Optional.of(user));
        when(jwtUtil.generateValueSignature("u1")).thenReturn("sig");

        mockMvc.perform(get("/api/v1/user/id/{id}", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.username").value("anna"));
    }

    @Test
    void shouldReturnPreconditionRequiredWhenRenameWithoutIfMatch() throws Exception {
        String body = objectMapper.writeValueAsString(new EditUserDTO("Nowe Imie"));

        mockMvc.perform(patch("/api/v1/user/id/{id}/rename", "u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isPreconditionRequired());
    }

    @Test
    void shouldRenameUserWhenIfMatchIsValid() throws Exception {
        User user = new User("anna", "encoded", "Anna", true);
        user.setId("u1");

        when(userViewPort.findUserById("u1")).thenReturn(Optional.of(user));
        when(jwtUtil.verifyValueSignature(anyString(), anyString())).thenReturn(true);

        String body = objectMapper.writeValueAsString(new EditUserDTO("Nowe Imie"));

        mockMvc.perform(patch("/api/v1/user/id/{id}/rename", "u1")
                        .header("If-Match", "sig")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }
}
