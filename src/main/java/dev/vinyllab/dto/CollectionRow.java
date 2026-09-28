package dev.vinyllab.dto;

import java.time.LocalDate;
import java.util.List;

public record CollectionRow(
    Long id,
    Long albumId,
    String albumTitle,
    String artistName,
    String condition,
    LocalDate acquiredOn,
    String notes,
    List<String> genres
) {
}
