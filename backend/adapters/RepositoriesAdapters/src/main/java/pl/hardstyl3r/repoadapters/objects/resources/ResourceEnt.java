package pl.hardstyl3r.repoadapters.objects.resources;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "#{@environment.getProperty('pas.mongodb.collection.resources')}")
public abstract class ResourceEnt {
    @Id
    private String id;
    private String name;
    private String description;

    protected ResourceEnt() {

    }

    public ResourceEnt(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public ResourceEnt(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ResourceEnt{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceEnt resourceEnt = (ResourceEnt) o;

        return Objects.equals(id, resourceEnt.id);
    }
}
