package pl.hardstyl3r.pas.v1.dto;

public record CreateResourceDTO(
        String type,
        String name,
        String description,
        String author,
        String isbn,
        Integer issueNumber,
        String releaseDate
) {
}
