package pl.hardstyl3r.pas.v1.dto;

import pl.hardstyl3r.pas.v1.objects.User; // Z DomainModel
import java.util.List;

public class UserConverter {

    public static UserDTO dtoFromUser(User user) {
        if (user == null) return null;
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.isActive(),
                user.getRole()
        );
    }

    public static List<UserDTO> dtoFromUsers(List<User> users) {
        return users.stream()
                .map(UserConverter::dtoFromUser)
                .toList();
    }
}