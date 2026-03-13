package pl.hardstyl3r.pas.v1.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserValidationException;
import pl.hardstyl3r.pas.v1.repositories.UserRepository;
import pl.hardstyl3r.repoadapters.objects.UserEnt;
import pl.hardstyl3r.repoadapters.objects.UserEntRole;

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

    public void registerUser(UserEnt user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserEntRole.CLIENT);
        user.setActive(false);
        userRepository.save(user);
    }

    public void changeUserRole(String id, UserEntRole newRole) {
        UserEnt user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setRole(newRole);
        userRepository.update(user);
    }

    public Optional<UserEnt> findUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<UserEnt> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserEnt> findAll() {
        return userRepository.findAll();
    }

    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

    public void userActivationById(String id, boolean active) {
        UserEnt user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setActive(active);
        userRepository.update(user);
    }

    public List<UserEnt> searchForUsersByUsername(String search) {
        return userRepository.findByUsernameContaining(search);
    }

    public void renameUserById(String id, String newName) {
        UserEnt user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        user.setName(newName);
        userRepository.update(user);
    }

    public void changePassword(String id, String oldPassword, String newPassword) {
        UserEnt user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserValidationException("Obecne hasło jest niepoprawne.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.update(user);
    }

}
