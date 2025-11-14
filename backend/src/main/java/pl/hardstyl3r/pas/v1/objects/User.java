package pl.hardstyl3r.pas.v1.objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "#{@environment.getProperty('pas.mongodb.collection.users')}")
public class User {
    @Id
    private String id;
    private String username;
    private String name;
    private boolean active;

    protected User() {
    }

    public User(String username, String name, boolean active) {
        this.username = username;
        this.name = name;
        this.active = active;
    }

    public User(String id, String username, String name, boolean active) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
