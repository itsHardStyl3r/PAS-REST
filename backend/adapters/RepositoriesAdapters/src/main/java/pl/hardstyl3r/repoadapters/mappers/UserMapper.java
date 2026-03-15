package pl.hardstyl3r.repoadapters.mappers;

import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.repoadapters.objects.UserEnt;
import pl.hardstyl3r.repoadapters.objects.UserEntRole;

public class UserMapper {

    public static User toDomain(UserEnt ent) {
        if (ent == null) return null;
        User user = new User(
                ent.getUsername(),
                ent.getPassword(),
                ent.getName(),
                ent.isActive()
        );
        user.setId(ent.getId());
        if (ent.getRole() != null) {
            user.setRole(UserRole.valueOf(ent.getRole().name()));
        }
        return user;
    }

    public static UserEnt toEntity(User domain) {
        if (domain == null) return null;
        UserEnt ent = new UserEnt(
                domain.getUsername(),
                domain.getPassword(),
                domain.getName(),
                domain.isActive()
        );
        ent.setId(domain.getId());
        if (domain.getRole() != null) {
            ent.setRole(UserEntRole.valueOf(domain.getRole().name()));
        }
        return ent;
    }
}