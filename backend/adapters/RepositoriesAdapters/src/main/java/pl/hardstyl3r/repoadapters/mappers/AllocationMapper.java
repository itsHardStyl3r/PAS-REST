package pl.hardstyl3r.repoadapters.mappers;

import pl.hardstyl3r.pas.v1.objects.Allocation;
import pl.hardstyl3r.repoadapters.objects.AllocationEnt;

public class AllocationMapper {

    public static Allocation toDomain(AllocationEnt ent) {
        if (ent == null) return null;
        return new Allocation(
                ent.getUserId(),
                ent.getResourceId()
        );
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