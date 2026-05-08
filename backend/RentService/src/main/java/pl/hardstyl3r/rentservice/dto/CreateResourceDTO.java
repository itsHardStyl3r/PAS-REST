package pl.hardstyl3r.rentservice.dto;

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
