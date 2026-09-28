package dev.vinyllab.dto;

import java.util.List;

public record ArtistView(
    Long id,
    String name,
    String country,
    String biography,
    long albumCount,
    List<AlbumView> albums
) {
}
