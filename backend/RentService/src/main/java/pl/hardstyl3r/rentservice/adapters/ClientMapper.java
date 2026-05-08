package pl.hardstyl3r.rentservice.adapters;

import pl.hardstyl3r.rentservice.domain.Client;
import pl.hardstyl3r.rentservice.domain.ClientRole;

public class ClientMapper {

    public static Client toDomain(ClientEnt ent) {
        if (ent == null) return null;
        Client client = new Client(ent.getId(), ent.getUsername(), ent.isActive());
        if (ent.getRole() != null) {
            client.setRole(ClientRole.valueOf(ent.getRole().name()));
        }
        return client;
    }

    public static ClientEnt toEntity(Client domain) {
        if (domain == null) return null;
        ClientEnt ent = new ClientEnt(
                domain.getUsername(),
                domain.isActive(),
                domain.getRole() != null ? ClientEntRole.valueOf(domain.getRole().name()) : ClientEntRole.CLIENT
        );
        ent.setId(domain.getId());
        return ent;
    }
}
