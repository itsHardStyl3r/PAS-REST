package pl.hardstyl3r.pas.v1.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.hardstyl3r.pas.v1.dto.UserDTO;
import pl.hardstyl3r.pas.v1.dto.UserConverter;
import pl.hardstyl3r.pas.v1.services.UserService;
import pl.hardstyl3r.repoadapters.objects.UserEnt;
import pl.hardstyl3r.repoadapters.objects.UserEntRole;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserGraphQLController {

    private final UserService userService;

    public UserGraphQLController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<UserDTO> users(@Argument UserFilter filter) {
        List<UserEnt> allUsers = userService.findAll();
        return allUsers.stream()
                .filter(user -> filter == null || matchesFilter(user, filter))
                .map(UserConverter::dtoFromUser)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(UserEnt user, UserFilter filter) {
        if (filter.username() != null && !user.getUsername().contains(filter.username())) return false;
        if (filter.name() != null && !user.getName().contains(filter.name())) return false;
        if (filter.active() != null && user.isActive() != filter.active()) return false;
        if (filter.role() != null && user.getRole() != filter.role()) return false;
        return true;
    }

    public record UserFilter(String username, String name, Boolean active, UserEntRole role) {}
}
