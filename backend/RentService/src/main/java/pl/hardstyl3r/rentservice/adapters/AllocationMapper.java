package pl.hardstyl3r.rentservice.adapters;

import pl.hardstyl3r.rentservice.domain.Allocation;

public class AllocationMapper {

    public static Allocation toDomain(AllocationEnt ent) {
        if (ent == null) return null;
        Allocation domain = new Allocation(ent.getUserId(), ent.getResourceId());
        domain.setId(ent.getId());
        domain.setStartTime(ent.getStartTime());
        domain.setEndTime(ent.getEndTime());
        return domain;
    }

    public static AllocationEnt toEntity(Allocation domain) {
        if (domain == null) return null;
        AllocationEnt ent = new AllocationEnt(domain.getUserId(), domain.getResourceId());
        ent.setId(domain.getId());
        ent.setStartTime(domain.getStartTime());
        ent.setEndTime(domain.getEndTime());
        return ent;
    }
}
