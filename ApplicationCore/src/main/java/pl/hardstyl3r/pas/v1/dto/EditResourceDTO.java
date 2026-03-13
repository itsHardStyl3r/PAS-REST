package pl.hardstyl3r.pas.v1.dto;

public record EditResourceDTO(
        String name,
        String description,
        String author,
        String isbn,
        Integer issueNumber,
        String releaseDate
) {
}
