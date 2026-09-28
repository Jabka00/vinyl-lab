package dev.vinyllab.dto;

import dev.vinyllab.model.Role;
import java.time.LocalDateTime;

public record UserCard(
    Long id,
    String username,
    String email,
    Role role,
    LocalDateTime createdAt
) {
}
