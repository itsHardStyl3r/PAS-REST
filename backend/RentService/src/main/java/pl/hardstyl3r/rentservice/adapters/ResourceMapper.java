package pl.hardstyl3r.rentservice.adapters;

import pl.hardstyl3r.rentservice.adapters.resource.BookEnt;
import pl.hardstyl3r.rentservice.adapters.resource.NewspaperEnt;
import pl.hardstyl3r.rentservice.adapters.resource.PeriodicalEnt;
import pl.hardstyl3r.rentservice.adapters.resource.ResourceEnt;
import pl.hardstyl3r.rentservice.domain.resource.Book;
import pl.hardstyl3r.rentservice.domain.resource.Newspaper;
import pl.hardstyl3r.rentservice.domain.resource.Periodical;
import pl.hardstyl3r.rentservice.domain.resource.Resource;

public class ResourceMapper {

    public static Resource toDomain(ResourceEnt ent) {
        return switch (ent) {
            case null -> null;
            case BookEnt b -> new Book(b.getId(), b.getName(), b.getDescription(), b.getAuthor(), b.getIsbn());
            case PeriodicalEnt p -> new Periodical(p.getId(), p.getName(), p.getDescription(), p.getIssueNumber());
            case NewspaperEnt n -> new Newspaper(n.getId(), n.getName(), n.getDescription(), n.getReleaseDate());
            default -> null;
        };
    }

    public static ResourceEnt toEntity(Resource domain) {
        return switch (domain) {
            case null -> null;
            case Book b -> new BookEnt(b.getId(), b.getName(), b.getDescription(), b.getAuthor(), b.getIsbn());
            case Periodical p -> new PeriodicalEnt(p.getId(), p.getName(), p.getDescription(), p.getIssueNumber());
            case Newspaper n -> new NewspaperEnt(n.getId(), n.getName(), n.getDescription(), n.getReleaseDate());
            default -> null;
        };
    }
}
