package dev.vinyllab.dto;

import java.util.List;

public record AlbumView(
    Long id,
    String title,
    String artistName,
    Long artistId,
    int releaseYear,
    String label,
    List<String> genres,
    double averageRating,
    long ratingCount,
    Integer myScore,
    long copiesInCollection
) {
}
