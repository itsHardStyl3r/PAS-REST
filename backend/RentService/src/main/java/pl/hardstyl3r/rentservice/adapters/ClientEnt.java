package pl.hardstyl3r.rentservice.adapters;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "#{@environment.getProperty('pas.mongodb.collection.clients')}")
public class ClientEnt {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private boolean active = false;
    private ClientEntRole role = ClientEntRole.CLIENT;

    protected ClientEnt() {
    }

    public ClientEnt(String username, boolean active, ClientEntRole role) {
        this.username = username;
        this.active = active;
        this.role = role;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public ClientEntRole getRole() { return role; }
    public void setRole(ClientEntRole role) { this.role = role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEnt clientEnt = (ClientEnt) o;
        return Objects.equals(id, clientEnt.id);
    }

    @Override
    public String toString() {
        return "ClientEnt{id='" + id + "', username='" + username + "', active=" + active + ", role=" + role + '}';
    }
}
