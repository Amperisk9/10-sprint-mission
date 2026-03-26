package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper mapper;

  @Transactional
  @Override
  public UserStatusDto createUserStatus(UserStatusDto.UserStatusCreateRequest createReq) {
    UUID userId = createReq.userId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    userStatusRepository.findByUserId(userId)
        .ifPresent(u -> {
          throw new BusinessLogicException(ErrorCode.USERSTATUS_ALREADY_EXISTS);
        });

    UserStatus status = new UserStatus();
    status.updateUser(user);
    userStatusRepository.save(status);

    return toResponse(status);
  }

  @Override
  public UserStatusDto findById(UUID uuid) {
    return userStatusRepository.findById(uuid)
        .map(this::toResponse)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public UserStatusDto updateUserStatus(UUID uuid,
      UserStatusDto.UserStatusUpdateRequest updateReq) {
    UserStatus userStatus = userStatusRepository.findById(uuid)
        .orElseThrow(() -> {
          log.warn("[Service] UserStatus not found: id={}", uuid);
          return new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND);
        });

    userStatus.updateLastActiveAt(updateReq.newLastActiveAt());
    userStatusRepository.save(userStatus);
    log.info("[Service] updateUserStatus: 유저 상태 수정 성공");

    return toResponse(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateUserStatusByUserId(UUID userId,
      UserStatusDto.UserStatusUpdateRequest updateReq) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.warn("[Service] UserStatus not found: userId={}", userId);
          return new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND);
        });

    userStatus.updateLastActiveAt(updateReq.newLastActiveAt());
    userStatusRepository.save(userStatus);
    log.info("[Service] updateUserStatus: 유저 상태 수정 성공");

    return toResponse(userStatus);
  }

  @Transactional
  @Override
  public void deleteUserStatusById(UUID uuid) {
    UserStatus userStatus = userStatusRepository.findById(uuid)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USERSTATUS_NOT_FOUND));

    userStatusRepository.deleteById(uuid);
  }

  private UserStatusDto toResponse(UserStatus userStatus) {
    return mapper.toDto(userStatus);
  }
}
