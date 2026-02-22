package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class UserDto {
    private UserDto() {}

    public record LoginRequest(String accountId, String password) {}
    public record createRequest(String accountId, String password, @NotEmpty String username, @NotEmpty @Email String email) {}
    public record updateRequest(String accountId, String password, String username, String email) {}
    public record userResponse(UUID uuid, Instant createdAt, Instant updatedAt,
                               String accountId, String username, String email,
                               UUID profileId, boolean online,
                               List<UUID> joinedChannels,
                               List<UUID> messageHistory) {}
}
