package pl.hardstyl3r.pas.v1.viewports;

import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserViewPort {
    void registerUser(User user);

    void changeUserRole(String id, UserRole newRole);

    Optional<User> findUserById(String id);

    Optional<User> findUserByUsername(String username);

    List<User> findAll();

    void deleteUserById(String id);

    void userActivationById(String id, boolean active);

    List<User> searchForUsersByUsername(String search);

    void renameUserById(String id, String newName);

    void changePassword(String id, String oldPassword, String newPassword);
}

