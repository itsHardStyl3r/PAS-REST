package pl.hardstyl3r.pas.v1.services;

import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.objects.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final List<User> users;

    public UserService() {
        users = new ArrayList<>();

        User ania = new User(1, "anna", "Ania");
        User marek = new User(2, "marek", "Marek");
        User pawel = new User(3, "pawel", "Paweł");

        users.addAll(Arrays.asList(ania, marek, pawel));
    }

    public Optional<User> findUserById(Integer id) {
        Optional<User> optional = Optional.empty();
        for (User found : users)
            if (found.getId() == id) return Optional.of(found);
        return optional;
    }

    public Optional<User> findUserByUsername(String username) {
        Optional<User> optional = Optional.empty();
        for (User found : users)
            if (found.getUsername().equals(username)) return Optional.of(found);
        return optional;
    }

    public List<User> findAll() {
        return users;
    }

    public void insertUser(User user) {
        users.add(user);
    }

    public void deleteUserById(Integer id) {
        users.stream().filter(user -> user.getId() == id).findFirst().ifPresent(users::remove);
    }

    public void userActivationById(Integer id, boolean active) {
        Optional<User> user = findUserById(id);
        if (user.isEmpty()) return;
        user.get().setActive(active);
    }

    public List<User> searchForUsersByUsername(String search) {
        return users.stream().filter(user -> user.getUsername().toLowerCase().contains(search.toLowerCase())).toList();
    }

    public void renameUserById(Integer id, String newName) {
        Optional<User> user = findUserById(id);
        if (user.isEmpty()) return;
        user.get().setName(newName);
    }
}
