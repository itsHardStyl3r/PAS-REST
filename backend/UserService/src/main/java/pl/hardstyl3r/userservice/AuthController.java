package pl.hardstyl3r.userservice;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.hardstyl3r.userservice.dto.JwtResponse;
import pl.hardstyl3r.userservice.dto.LoginRequest;
import pl.hardstyl3r.userservice.dto.RegisterRequest;
import pl.hardstyl3r.userservice.domain.User;
import pl.hardstyl3r.userservice.domain.exception.UserNotFoundException;
import pl.hardstyl3r.userservice.domain.exception.UsernameIsTakenException;
import pl.hardstyl3r.userservice.security.UserDetailsServiceImpl;
import pl.hardstyl3r.userservice.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken, user.getId(), user.getUsername(), user.getRole().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.findUserByUsername(registerRequest.username()).isPresent()) {
            throw new UsernameIsTakenException(registerRequest.username());
        }

        User user = new User(registerRequest.username(), registerRequest.password(), registerRequest.name(), false);
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Brak refresh tokena");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails);

                User user = userService.findUserByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

                return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken, user.getId(), user.getUsername(), user.getRole().name()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Nieprawidłowy lub wygasły Refresh Token");
        }

        return ResponseEntity.status(401).body("Refresh Token wygasł!");
    }
}
