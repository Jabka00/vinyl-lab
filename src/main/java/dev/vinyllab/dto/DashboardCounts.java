package dev.vinyllab.dto;

public record DashboardCounts(
    long users,
    long artists,
    long albums,
    long genres,
    long items,
    long ratings
) {
}
