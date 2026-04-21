package pl.hardstyl3r.pas.soap.api;

import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.pas.v1.objects.User;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;

public final class SoapMapper {

    private SoapMapper() {
    }

    public static SoapUser fromUser(User user) {
        SoapUser soapUser = new SoapUser();
        soapUser.setId(user.getId());
        soapUser.setUsername(user.getUsername());
        soapUser.setName(user.getName());
        soapUser.setActive(user.isActive());
        soapUser.setRole(user.getRole() == null ? null : user.getRole().name());
        return soapUser;
    }

    public static SoapResource fromResource(Resource resource) {
        SoapResource soapResource = new SoapResource();
        soapResource.setId(resource.getId());
        soapResource.setName(resource.getName());
        soapResource.setDescription(resource.getDescription());
        soapResource.setType(resource.getClass().getSimpleName().toUpperCase());
        return soapResource;
    }

    public static SoapAllocation fromAllocation(Allocation allocation) {
        SoapAllocation soapAllocation = new SoapAllocation();
        soapAllocation.setId(allocation.getId());
        soapAllocation.setUserId(allocation.getUserId());
        soapAllocation.setResourceId(allocation.getResourceId());
        soapAllocation.setStartTime(allocation.getStartTime() == null ? null : allocation.getStartTime().toString());
        soapAllocation.setEndTime(allocation.getEndTime() == null ? null : allocation.getEndTime().toString());
        return soapAllocation;
    }
}

