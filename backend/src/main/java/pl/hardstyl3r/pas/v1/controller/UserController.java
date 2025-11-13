package pl.hardstyl3r.pas.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.dto.CreateUserDTO;
import pl.hardstyl3r.pas.v1.dto.EditUserDTO;
import pl.hardstyl3r.pas.v1.dto.UserConverter;
import pl.hardstyl3r.pas.v1.dto.UserDTO;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserValidationException;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/id/{id}")
    public UserDTO getUserById(@PathVariable String id) {
        return userService.findUserById(id)
                .map(UserConverter::dtoFromUser)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @GetMapping("/user/username/{username}")
    public UserDTO getUserByName(@PathVariable String username) {
        return userService.findUserByUsername(username)
                .map(UserConverter::dtoFromUser)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        return UserConverter.dtoFromUsers(userService.findAll());
    }

    @PostMapping("/user")
    public void createUser(@RequestBody CreateUserDTO userDTO) {
        if (userDTO.username() == null || userDTO.username().isBlank()) {
            throw new UserValidationException("Username cannot be blank");
        }
        if (userDTO.name() == null || userDTO.name().isBlank()) {
            throw new UserValidationException("Name cannot be blank");
        }
        User user = new User(
                userDTO.username(),
                userDTO.name(),
                userDTO.active()
        );
        userService.insertUser(user);
    }

    @DeleteMapping("/user/id/{id}")
    public void deleteUser(@PathVariable String id) {
        if (userService.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userService.deleteUserById(id);
    }

    @PatchMapping("/user/id/{id}/deactivate")
    public void deactivateUser(@PathVariable String id) {
        if (userService.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userService.userActivationById(id, false);
    }

    @PatchMapping("/user/id/{id}/activate")
    public void activateUser(@PathVariable String id) {
        if (userService.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userService.userActivationById(id, true);
    }

    @GetMapping("/user/search/{search}")
    public List<UserDTO> searchForUser(@PathVariable String search) {
        return UserConverter.dtoFromUsers(userService.searchForUsersByUsername(search));
    }

    @PatchMapping("/user/id/{id}/rename")
    public void renameUser(@PathVariable String id, @RequestBody EditUserDTO userDTO) {
        if (userService.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        if (userDTO.name() == null || userDTO.name().isBlank()) {
            throw new UserValidationException("Name cannot be blank");
        }
        userService.renameUserById(id, userDTO.name());
    }
}
