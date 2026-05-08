package pl.hardstyl3r.rentservice.ports.driven;

import pl.hardstyl3r.rentservice.domain.Client;

import java.util.List;
import java.util.Optional;

public interface ClientPort {
    List<Client> findAll();
    Optional<Client> findById(String id);
    Optional<Client> findByUsername(String username);
    Client save(Client client);
    void deleteById(String id);
}
