package pl.hardstyl3r.rentservice.dto;

public record EditResourceDTO(
        String name,
        String description,
        String author,
        String isbn,
        Integer issueNumber,
        String releaseDate
) {
}
