package pl.hardstyl3r.pas.v1.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.appports.UserPort;
import pl.hardstyl3r.pas.v1.exceptions.*;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.viewports.UserViewPort;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserViewPort {
    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserPort userPort, PasswordEncoder passwordEncoder) {
        this.userPort = userPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.CLIENT);
        user.setActive(false);
        userPort.save(user);
    }

    @Override
    public void changeUserRole(String id, UserRole newRole) {
        User user = userPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setRole(newRole);
        userPort.update(user);
    }

    @Override
    public Optional<User> findUserById(String id) {
        return userPort.findById(id);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userPort.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userPort.findAll();
    }

    @Override
    public void deleteUserById(String id) {
        userPort.deleteById(id);
    }

    @Override
    public void userActivationById(String id, boolean active) {
        User user = userPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setActive(active);
        userPort.update(user);
    }

    @Override
    public List<User> searchForUsersByUsername(String search) {
        return userPort.findByUsernameContaining(search);
    }

    @Override
    public void renameUserById(String id, String newName) {
        User user = userPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setName(newName);
        userPort.update(user);
    }

    @Override
    public void changePassword(String id, String oldPassword, String newPassword) {
        User user = userPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserValidationException("Obecne hasło jest niepoprawne.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userPort.update(user);
    }
}