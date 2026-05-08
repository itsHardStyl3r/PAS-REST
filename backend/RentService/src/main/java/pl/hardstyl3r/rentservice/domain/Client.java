package pl.hardstyl3r.rentservice.domain;

public class Client {
    private final String id;
    private final String username;
    private final boolean active;

    public Client(String id, String username, boolean active) {
        this.id = id;
        this.username = username;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return active;
    }
}
