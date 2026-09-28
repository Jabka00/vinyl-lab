package dev.vinyllab.dto;

import java.util.List;

public record CollectionStats(
    long total,
    List<StatSlice> genres,
    List<StatSlice> conditions
) {
}
