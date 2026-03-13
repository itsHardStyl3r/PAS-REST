package pl.hardstyl3r.pas.v1.objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "#{@environment.getProperty('pas.mongodb.collection.allocations')}")
public class Allocation {
    @Id
    private String id;
    private String userId;
    private String resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    protected Allocation() {

    }

    public Allocation(String userId, String resourceId) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Allocation{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allocation that = (Allocation) o;

        return Objects.equals(id, that.id);
    }
}
