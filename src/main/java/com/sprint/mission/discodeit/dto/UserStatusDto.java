package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record UserStatusDto(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

  public record UserStatusCreateRequest(@NotBlank UUID userId) {

  }

  public record UserStatusUpdateRequest(@NotBlank Instant newLastActiveAt) {

  }
}