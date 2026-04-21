package pl.hardstyl3r.pas.v1.viewports;

public record EditResourceCommand(
        String name,
        String description,
        String author,
        String isbn,
        Integer issueNumber,
        String releaseDate
) {
}

