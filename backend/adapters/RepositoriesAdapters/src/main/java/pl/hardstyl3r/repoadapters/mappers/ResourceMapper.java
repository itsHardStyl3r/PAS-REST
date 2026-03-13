package pl.hardstyl3r.repoadapters.mappers;

import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;
import pl.hardstyl3r.repoadapters.objects.resources.BookEnt;
import pl.hardstyl3r.repoadapters.objects.resources.ResourceEnt;

public class ResourceMapper {

    public static Resource toDomain(ResourceEnt ent) {
        if (ent == null) return null;

        if (ent instanceof BookEnt b) {
            return new Book(
                    b.getId(),
                    b.getName(),
                    b.getDescription(),
                    b.getAuthor(),
                    b.getIsbn()
            );
        }
        /// WIP
        return null;
    }

    public static ResourceEnt toEntity(Resource domain) {
        if (domain == null) return null;

        if (domain instanceof Book b) {
            return new BookEnt(
                    b.getId(),
                    b.getName(),
                    b.getDescription(),
                    b.getAuthor(),
                    b.getIsbn()
            );
        }

        /// WIP reszta typów
        return null;
    }
}