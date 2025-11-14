package pl.hardstyl3r.pas.v1.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UsernameIsTakenException;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public void insertUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameIsTakenException(user.getUsername());
        }
        userRepository.save(user);
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
        user.setName(newName);
        userRepository.update(user);
    }
}
