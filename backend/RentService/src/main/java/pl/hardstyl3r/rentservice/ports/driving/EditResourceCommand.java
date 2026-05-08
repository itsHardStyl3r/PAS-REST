package pl.hardstyl3r.rentservice.ports.driving;

public record EditResourceCommand(
        String name,
        String description,
        String author,
        String isbn,
        Integer issueNumber,
        String releaseDate
) {
}
