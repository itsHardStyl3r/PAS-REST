package pl.hardstyl3r.pas.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.dto.CreateUserDTO;
import pl.hardstyl3r.pas.v1.dto.EditUserDTO;
import pl.hardstyl3r.pas.v1.dto.UserConverter;
import pl.hardstyl3r.pas.v1.dto.UserDTO;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.services.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/id/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.findUserById(id);
        return user.map(UserConverter::dtoFromUser).orElse(null);
    }

    @GetMapping("/user/username/{username}")
    public UserDTO getUserByName(@PathVariable String username) {
        Optional<User> user = userService.findUserByUsername(username);
        return user.map(UserConverter::dtoFromUser).orElse(null);
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return UserConverter.dtoFromUsers(userService.findAll());
    }

    @PostMapping("/user")
    public void createUser(@RequestBody CreateUserDTO userDTO) {
        User user = new User(
                (userService.findAll().isEmpty() ? 1 : userService.findAll().size() + 1),
                userDTO.username(),
                userDTO.name(),
                userDTO.active()
        );
        userService.insertUser(user);
    }

    @DeleteMapping("/user/id/{id}")
    public void deleteUser(@PathVariable Integer id) {
        if (userService.findUserById(id).isEmpty()) return;
        userService.deleteUserById(id);
    }

    @PatchMapping("/user/id/{id}/deactivate")
    public void deactivateUser(@PathVariable Integer id) {
        userService.userActivationById(id, false);
    }

    @PatchMapping("/user/id/{id}/activate")
    public void activateUser(@PathVariable Integer id) {
        userService.userActivationById(id, true);
    }

    @GetMapping("/user/search/{search}")
    public List<User> searchForUser(@PathVariable String search) {
        return userService.searchForUsersByUsername(search);
    }

    @PatchMapping("/user/id/{id}/rename")
    public void renameUser(@PathVariable Integer id, @RequestBody EditUserDTO userDTO) {
        userService.renameUserById(id, userDTO.name());
    }
}
