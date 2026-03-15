package pl.hardstyl3r.repoadapters.adapters;

import org.springframework.stereotype.Component;
import pl.hardstyl3r.pas.appports.UserPort;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.repoadapters.mappers.UserMapper;
import pl.hardstyl3r.repoadapters.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserAdapter implements UserPort {

    private final UserRepository userRepository;

    public UserAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        return UserMapper.toDomain(userRepository.save(UserMapper.toEntity(user)));
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public void update(User user) {
        userRepository.save(UserMapper.toEntity(user));
    }

    @Override
    public List<User> findByUsernameContaining(String search) {
        return userRepository.findByUsernameContaining(search).stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }
}