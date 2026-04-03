package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;

  @Nested
  class findByChannelIdOrderByCreatedAtDesc {

  }

  @Nested
  class findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc {

  }

  @Nested
  class findFirstByChannelIdOrderByCreatedAtDesc {

    @Test
    @DisplayName("생성 시간이 제일 최근것인 메세지 반환")
    void should_return_latest_message_optional() throws Exception {
      // given
      User user = new User("A", "AA", "A@gmail.com");
      userRepository.save(user);
      Channel channel = Channel.of("name", "description");
      channelRepository.save(channel);

      Message msg1 = new Message(channel, user, "first");
      messageRepository.save(msg1);
      Thread.sleep(100);
      Message latest = new Message(channel, user, "latest");
      messageRepository.save(latest);

      // when
      Optional<Message> found = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
          channel.getId());

      // then
      assertThat(found).hasValueSatisfying(m -> {
        assertThat(m.getContent()).isEqualTo(latest.getContent());
        assertThat(m.getCreatedAt()).isEqualTo(latest.getCreatedAt());
      });

    }

    @Test
    @DisplayName("메세지 없을 때의 빈 객체 반환")
    void should_return_empty_when_no_messages() {
      // when
      Optional<Message> found = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
          UUID.randomUUID());

      // then
      assertThat(found).isEmpty();
    }
  }
}