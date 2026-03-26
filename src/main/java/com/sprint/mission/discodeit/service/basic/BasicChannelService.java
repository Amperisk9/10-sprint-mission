package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelMapper mapper;

  @Transactional
  @Override
  public ChannelDto createChannel(ChannelDto.PrivateChannelCreateRequest channelPrivateReq) {
    log.debug("[Service] 비공개채널 생성 시작: participants={}", channelPrivateReq.participantIds());
    List<UUID> participantIds = channelPrivateReq.participantIds();

    // 참여자 목록의 유저가 user DB에 있는지 확인
    List<User> users = userRepository.findAllById(participantIds);
    if (participantIds.size() != users.size()) {
      throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
    }

    Channel privateChannel = Channel.of(participantIds);
    channelRepository.save(privateChannel);
    log.debug("[Service] 비공개채널 저장 완료: id={}", privateChannel.getId());

    // ReadStatus 생성
    List<ReadStatus> readStatuses = users.stream()
        .map(user -> new ReadStatus(user, privateChannel)).toList();
    readStatusRepository.saveAll(readStatuses);
    log.debug("[Service] ReadStatuses 저장 완료: channelId={}", privateChannel.getId());

    log.info("[Service] 비공개채널 생성 성공: id={}", privateChannel.getId());
    return toResponse(privateChannel);
  }

  @Transactional
  @Override
  public ChannelDto createChannel(ChannelDto.PublicChannelCreateRequest channelPublicReq) {
    log.debug("[Service] 공개채널 생성 시작: name={}, description={}",
        channelPublicReq.name(), channelPublicReq.description());
    validateDuplicateName(channelPublicReq.name());

    Channel publicChannel = Channel.of(channelPublicReq.name(), channelPublicReq.description());
    channelRepository.save(publicChannel);
    log.debug("[Service] 공개채널 저장 완료: id={}", publicChannel.getId());

    log.info("[Service] 공개채널 생성 성공: id={}, name={}",
        publicChannel.getId(), publicChannel.getName());
    return toResponse(publicChannel);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    getUserOrThrow(userId);

    // PUBLIC 채널 전부 + userId가 참여한 PRIVATE 채널(JPQL 처리)
//        List<ChannelDto.RemoveParticipants> channels = channelRepository.findAllByUserId(userId);
//        if (channels.isEmpty()) return List.of();
//        return mapper.toDto(channels);

    return channelRepository.findAll().stream()
        // PUBLIC 채널 전부 + userId가 참여한 PRIVATE 채널
        .filter(c -> Objects.equals(c.getType(), ChannelType.PUBLIC)
            || readStatusRepository.existsByChannelIdAndUserId(c.getId(), userId))
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public ChannelDto updateChannel(UUID uuid, ChannelDto.PublicChannelUpdateRequest channelReq) {
    log.debug("[Service] 채널 수정 시작: id={}, newName={}, newDescription={}",
        uuid, channelReq.newName(), channelReq.newDescription());
    Channel channel = getChannelOrThrow(uuid);

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new BusinessLogicException(ErrorCode.PRIVATE_CHANNEL_NOT_EDITABLE);
    }

    // name 중복성 검사
    if (channelReq.newName() != null && !Objects.equals(channel.getName(), channelReq.newName())) {
      validateDuplicateName(channelReq.newName());
    }

    Optional.ofNullable(channelReq.newName()).ifPresent(channel::updateName);
    Optional.ofNullable(channelReq.newDescription()).ifPresent(channel::updateDescription);
    channelRepository.save(channel);
    log.debug("[Service] 수정된 채널 저장 완료: id={}", channel.getId());

    log.info("[Service] 채널 수정 성공: id={}, name={}, description={}",
        channel.getId(), channel.getName(), channel.getDescription());
    return toResponse(channel);
  }

  @Transactional
  @Override
  public void deleteChannel(UUID uuid) {
    log.debug("[Service] 채널 삭제 시작: id={}", uuid);
    getChannelOrThrow(uuid);

    channelRepository.deleteById(uuid);
    log.info("[Service] 채널 삭제 성공: id={}", uuid);
  }

  private void validateDuplicateName(String name) {
    channelRepository.findByName(name)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.DUPLICATE_TITLE));
  }

  private Channel getChannelOrThrow(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
  }

  private ChannelDto toResponse(Channel channel) {
    return mapper.toDto(channel);
  }
}
