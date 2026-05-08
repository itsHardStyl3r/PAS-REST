package pl.hardstyl3r.rentservice.adapters;

import org.springframework.stereotype.Component;
import pl.hardstyl3r.rentservice.domain.Client;
import pl.hardstyl3r.rentservice.ports.driven.ClientPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientAdapter implements ClientPort {

    private final ClientRepository clientRepository;

    public ClientAdapter(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll().stream().map(ClientMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Client> findById(String id) {
        return clientRepository.findById(id).map(ClientMapper::toDomain);
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username).map(ClientMapper::toDomain);
    }

    @Override
    public Client save(Client client) {
        return ClientMapper.toDomain(clientRepository.save(ClientMapper.toEntity(client)));
    }

    @Override
    public void deleteById(String id) {
        clientRepository.deleteById(id);
    }
}
