package pl.hardstyl3r.webpas.dto;

public class AllocationRequest {
    private String userId;
    private String resourceId;

    public AllocationRequest() {
    }

    public AllocationRequest(String userId, String resourceId) {
        this.userId = userId;
        this.resourceId = resourceId;
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
}
