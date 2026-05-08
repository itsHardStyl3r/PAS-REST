package pl.hardstyl3r.rentservice.domain;

import java.util.Objects;

public class Client {
    private String id;
    private String username;
    private boolean active;
    private ClientRole role;

    public Client(String id, String username, boolean active) {
        this.id = id;
        this.username = username;
        this.active = active;
        this.role = ClientRole.CLIENT;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public ClientRole getRole() { return role; }
    public void setRole(ClientRole role) { this.role = role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public String toString() {
        return "Client{id='" + id + "', username='" + username + "', active=" + active + ", role=" + role + '}';
    }
}
