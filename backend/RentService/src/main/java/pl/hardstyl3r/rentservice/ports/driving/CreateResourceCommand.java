package pl.hardstyl3r.rentservice.ports.driving;

public record CreateResourceCommand(
        String type,
        String name,
        String description,
        String author,
        String isbn,
        Integer issueNumber,
        String releaseDate
) {
}
