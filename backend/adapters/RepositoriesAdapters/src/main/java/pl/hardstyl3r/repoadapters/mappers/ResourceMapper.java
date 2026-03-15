package pl.hardstyl3r.repoadapters.mappers;

import pl.hardstyl3r.pas.v1.objects.resources.Book;
import pl.hardstyl3r.pas.v1.objects.resources.Newspaper;
import pl.hardstyl3r.pas.v1.objects.resources.Periodical;
import pl.hardstyl3r.pas.v1.objects.resources.Resource;
import pl.hardstyl3r.repoadapters.objects.resources.BookEnt;
import pl.hardstyl3r.repoadapters.objects.resources.NewspaperEnt;
import pl.hardstyl3r.repoadapters.objects.resources.PeriodicalEnt;
import pl.hardstyl3r.repoadapters.objects.resources.ResourceEnt;

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