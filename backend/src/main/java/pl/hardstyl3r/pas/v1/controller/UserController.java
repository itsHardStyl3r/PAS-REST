package pl.hardstyl3r.pas.v1.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.pas.v1.dto.ChangePasswordRequest;
import pl.hardstyl3r.pas.v1.dto.EditUserDTO;
import pl.hardstyl3r.pas.v1.dto.UserConverter;
import pl.hardstyl3r.pas.v1.dto.UserDTO;
import pl.hardstyl3r.pas.v1.exceptions.UserNotFoundException;
import pl.hardstyl3r.pas.v1.exceptions.UserValidationException;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.UserRole;
import pl.hardstyl3r.pas.v1.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173")
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

    @DeleteMapping("/user/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUserById(id);
    }

    @PatchMapping("/user/id/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateUser(@PathVariable String id) {
        if (userService.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userService.userActivationById(id, false);
    }

    @PatchMapping("/user/id/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public void renameUser(@PathVariable String id, @Valid @RequestBody EditUserDTO userDTO) {
        if (userService.findUserById(id).isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userService.renameUserById(id, userDTO.name());
    }

    @PutMapping("/user/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRole(@PathVariable String id, @RequestParam("role") String role) {
        try {
            UserRole newRole = UserRole.valueOf(role.toUpperCase());
            userService.changeUserRole(id, newRole);
            return ResponseEntity.ok("User role updated successfully.");
        } catch (IllegalArgumentException e) {
            throw new UserValidationException("Invalid role: " + role);
        }
    }

    @PatchMapping("/user/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userService.changePassword(user.getId(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok("Hasło zostało zmienione.");
    }
}
