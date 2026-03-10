package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);
    List<Message> findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId, Instant cursor, Pageable pageable);
    Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);
    void deleteAllByChannelId(UUID channelId);
}
