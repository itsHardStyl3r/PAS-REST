package pl.hardstyl3r.pas.v1.objects;

public class User {

    private int id;
    private String username;
    private String name;
    private boolean active;

    public User() {
    }

    public User(int id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.active = true;
    }

    public User(int id, String username, String name, boolean active) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
