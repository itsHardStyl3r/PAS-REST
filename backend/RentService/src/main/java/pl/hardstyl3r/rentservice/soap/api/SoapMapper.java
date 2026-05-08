package pl.hardstyl3r.rentservice.soap.api;

import pl.hardstyl3r.rentservice.domain.Allocation;
import pl.hardstyl3r.rentservice.domain.resource.Resource;

public final class SoapMapper {

    private SoapMapper() {
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
