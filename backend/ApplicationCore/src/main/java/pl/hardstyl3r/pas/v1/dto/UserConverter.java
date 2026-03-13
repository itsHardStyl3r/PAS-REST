package pl.hardstyl3r.pas.v1.dto;


import pl.hardstyl3r.repoadapters.objects.UserEnt;

import java.util.List;

public class UserConverter {

    public static UserDTO dtoFromUser(UserEnt user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.isActive(),
                user.getRole()
        );
    }

    public static List<UserDTO> dtoFromUsers(List<UserEnt> users) {
        return users.stream()
                .map(UserConverter::dtoFromUser)
                .toList();
    }
}
