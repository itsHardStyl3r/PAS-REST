package pl.hardstyl3r.repoadapters.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "#{@environment.getProperty('pas.mongodb.collection.users')}")
public class UserEnt {
    @Id
    private String id;

    @NotBlank(message = "Nazwa użytkownika nie może być pusta.")
    @Size(min = 3, max = 32, message = "Nazwa użytkownika musi mieć od 3 do 32 znaków.")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "Imię nie może być puste.")
    @Size(min = 3, max = 64, message = "Imię musi mieć od 3 do 64 znaków.")
    private String name;

    private boolean active = false;
    private UserEntRole role = UserEntRole.CLIENT;

    @JsonIgnore
    @NotBlank(message = "Hasło nie może być puste.")
    @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków.")
    private String password;

    protected UserEnt() {
    }

    public UserEnt(String username, String password, String name, boolean active) {
        this.username = username;
        this.password = password;
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

    public UserEntRole getRole() {
        return role;
    }

    public void setRole(UserEntRole role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEnt userEnt = (UserEnt) o;
        return Objects.equals(id, userEnt.id);
    }

    @Override
    public String toString() {
        return "UserEnt{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", role=" + role +
                '}';
    }
}
