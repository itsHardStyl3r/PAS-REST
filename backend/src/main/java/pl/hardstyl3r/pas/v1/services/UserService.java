package pl.hardstyl3r.pas.v1.services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.hardstyl3r.pas.v1.exceptions.UsernameIsTakenException;
import pl.hardstyl3r.pas.v1.objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final MongoCollection<Document> usersCollection;

    public UserService(MongoDatabase mongoDatabase, @Value("${pas.mongodb.collection.users}") String collectionName) {
        this.usersCollection = mongoDatabase.getCollection(collectionName);
    }

    private User documentToUser(Document doc) {
        if (doc == null) {
            return null;
        }
        return new User(
                doc.getObjectId("_id").toHexString(),
                doc.getString("username"),
                doc.getString("name"),
                doc.getBoolean("active")
        );
    }

    public Optional<User> findUserById(String id) {
        Document doc = usersCollection.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(documentToUser(doc));
    }

    public Optional<User> findUserByUsername(String username) {
        Document doc = usersCollection.find(Filters.eq("username", username)).first();
        return Optional.ofNullable(documentToUser(doc));
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        usersCollection.find().forEach(doc -> users.add(documentToUser(doc)));
        return users;
    }

    public void insertUser(User user) {
        if (findUserByUsername(user.getUsername()).isPresent()) {
            throw new UsernameIsTakenException(user.getUsername());
        }

        Document doc = new Document("username", user.getUsername())
                .append("name", user.getName())
                .append("active", user.isActive());
        usersCollection.insertOne(doc);
    }

    public void deleteUserById(String id) {
        usersCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
    }

    public void userActivationById(String id, boolean active) {
        usersCollection.updateOne(Filters.eq("_id", new ObjectId(id)), Updates.set("active", active));
    }

    public List<User> searchForUsersByUsername(String search) {
        List<User> users = new ArrayList<>();
        Pattern regex = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        usersCollection.find(Filters.regex("username", regex)).forEach(doc -> users.add(documentToUser(doc)));
        return users;
    }

    public void renameUserById(String id, String newName) {
        usersCollection.updateOne(Filters.eq("_id", new ObjectId(id)), Updates.set("name", newName));
    }
}
