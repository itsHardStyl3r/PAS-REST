package pl.hardstyl3r.pas.v1.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserValidationException;
import pl.hardstyl3r.pas.v1.exceptions.UsernameIsTakenException;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new UserValidationException("Username cannot be blank.");
        }
        if (user.getUsername().length() < 3 || user.getUsername().length() > 32) {
            throw new UserValidationException("Username must be between 3 and 32 characters.");
        }
        if (user.getName().length() < 3 || user.getName().length() > 64) {
            throw new UserValidationException("Name must be between 3 and 64 characters.");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new UserValidationException("Password cannot be blank.");
        }
        if (user.getPassword().length() < 8) {
            throw new UserValidationException("Password must be at least 8 characters long.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new UserValidationException("User name cannot be blank.");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameIsTakenException(user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.CLIENT);
        user.setActive(false);
        userRepository.save(user);
    }

    public void changeUserRole(String id, UserRole newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setRole(newRole);
        userRepository.update(user);
    }

    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

    public void userActivationById(String id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setActive(active);
        userRepository.update(user);
    }

    public List<User> searchForUsersByUsername(String search) {
        return userRepository.findByUsernameContaining(search);
    }

    public void renameUserById(String id, String newName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        if (newName == null || newName.isBlank()) {
            throw new UserValidationException("Username cannot be blank.");
        }
        if (newName.length() < 3 || newName.length() > 64) {
            throw new UserValidationException("Name must be between 3 and 64 characters.");
        }
        user.setName(newName);
        userRepository.update(user);
    }
}
