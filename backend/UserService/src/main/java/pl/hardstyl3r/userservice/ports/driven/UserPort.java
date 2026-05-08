package pl.hardstyl3r.userservice.ports.driven;

import pl.hardstyl3r.userservice.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserPort {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User save(User user);
    void deleteById(String id);
    void update(User user);
    List<User> findByUsernameContaining(String search);
}
