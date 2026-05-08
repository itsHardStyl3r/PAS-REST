package pl.hardstyl3r.rentservice.domain;

import pl.hardstyl3r.pas.v1.objects.User;

public final class ClientMapper {

    private ClientMapper() {
    }

    public static Client fromUser(User user) {
        return new Client(user.getId(), user.getUsername(), user.isActive());
    }
}
