package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper mapper;

  @Transactional
  @Override
  public ReadStatusDto createReadStatus(ReadStatusDto.ReadStatusCreateRequest createReq) {
    User user = userRepository.findById(createReq.userId())
        .orElseThrow(() -> {
          log.warn("[Service] User not found: id={}", createReq.userId());
          return new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
        });
    Channel channel = channelRepository.findById(createReq.channelId())
        .orElseThrow(() -> {
          log.warn("[Service] Channel not found: id={}", createReq.channelId());
          return new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND);
        });

    readStatusRepository.findAllByUserId(user.getId()).stream()
        .filter(r -> Objects.equals(r.getChannel().getId(), channel.getId()))
        .findFirst()
        .ifPresent(r -> {
          log.warn("[Service] ReadStatus is already: id={}", r.getId());
          throw new BusinessLogicException(ErrorCode.READSTATUS_ALREADY_EXISTS);
        });

    ReadStatus readStatus = new ReadStatus(user, channel);
    readStatusRepository.save(readStatus);
    log.info("[Service] createReadStatus: ReadStatus 저장 성공");

    return toResponse(readStatus);
  }

  @Override
  public ReadStatusDto findById(UUID uuid) {
    ReadStatus readStatus = readStatusRepository.findById(uuid)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.READSTATUS_NOT_FOUND));

    return toResponse(readStatus);
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
    }

    return readStatusRepository.findAllByUserId(userId).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public ReadStatusDto updateReadStatus(UUID uuid,
      ReadStatusDto.ReadStatusUpdateRequest updateReq) {
    ReadStatus readStatus = readStatusRepository.findById(uuid)
        .orElseThrow(() -> {
          log.warn("[Service] ReadStatus not found: id={}", uuid);
          return new BusinessLogicException(ErrorCode.READSTATUS_NOT_FOUND);
        });

    readStatus.updateLastReadAt(updateReq.newLastReadAt());
    readStatusRepository.save(readStatus);
    log.info("[Service] updateReadStatus: ReadStatus 수정 성공");

    return toResponse(readStatus);
  }

  @Transactional
  @Override
  public void deleteReadStatusById(UUID uuid) {
    readStatusRepository.deleteById(uuid);
    log.info("[Service] deleteReadStatus: ReadStatus 삭제 성공");
  }

  private ReadStatusDto toResponse(ReadStatus readStatus) {
    return mapper.toDto(readStatus);
  }
}

