package pl.hardstyl3r.rentservice.domain;

// Klasa w RentService - różni się strukturą od User w UserService (brak haseł)
public class Client {
    private String id;
    private String username;
    private boolean active;

    public Client(String id, String username, boolean active) {
        this.id = id;
        this.username = username;
        this.active = active;
    }
    // Gettery i settery
}
