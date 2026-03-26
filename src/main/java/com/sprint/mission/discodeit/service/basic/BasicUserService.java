package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper mapper;

  @Transactional
  @Override
  public UserDto createUser(UserDto.UserCreateRequest userReq, MultipartFile profileImage)
      throws IOException {
    log.info("[Service] createUser - START");
    log.debug("[Service] createUser - PARAMETERS: username={}, email={}, profileImage={}",
        userReq.username(), userReq.email(), profileImage);

    validateDuplicateUsername(userReq.username());
    validateDuplicateEmail(userReq.email());
    log.debug("[Service] createUser - LOGIC: username & email 중복 검증 완료");

    User user = new User(userReq.username(), userReq.password(), userReq.email());
    log.info("[Service] createUser - LOGIC: user 생성");

    // userStatus 관련
    UserStatus status = new UserStatus();
    user.updateStatus(status);
    log.info("[Service] createUser - LOGIC: userStatus 생성");

    // profile 이미지를 같이 추가하면
    processUpdateProfile(user, profileImage);
    userRepository.save(user);
    log.debug("[Service] createUser - LOGIC: user 저장 완료");

    log.info("[Service] createUser - END");
    return toDto(user);
  }

  @Override
  public List<UserDto> findAllUsers() {
    return userRepository.findAll().stream()
        .map(this::toDto).toList();
  }

  @Transactional
  @Override
  public UserDto updateUser(UUID uuid, UserDto.UserUpdateRequest userReq,
      MultipartFile profileImage) throws IOException {
    User user = userRepository.findById(uuid)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    // username과 mail 중복성 검사
    if (userReq.newUsername() != null && !Objects.equals(user.getUsername(),
        userReq.newUsername())) {
      validateDuplicateUsername(userReq.newUsername());
    }
    if (userReq.newEmail() != null && !Objects.equals(user.getEmail(), userReq.newEmail())) {
      validateDuplicateEmail(userReq.newEmail());
    }

    Optional.ofNullable(userReq.newUsername()).ifPresent(user::updateUserName);
    Optional.ofNullable(userReq.newPassword()).ifPresent(user::updatePassword);
    Optional.ofNullable(userReq.newEmail()).ifPresent(user::updateEmail);

    // 변경되는 프로필 이미지가 있으면
    processUpdateProfile(user, profileImage);

    userRepository.save(user);

    return toDto(user);
  }

  @Transactional
  @Override
  public void deleteUser(UUID uuid) {
    userRepository.findById(uuid)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    userRepository.deleteById(uuid);
  }

  private void validateDuplicateUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      log.warn("[Service] validate: Duplicate Username={}", username);
      throw new BusinessLogicException(ErrorCode.DUPLICATE_USERNAME);
    }
  }

  private void validateDuplicateEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      log.warn("[Service] validate: Duplicate Email={}", email);
      throw new BusinessLogicException(ErrorCode.DUPLICATE_EMAIL);
    }
  }

  private UserDto toDto(User user) {
    return mapper.toDto(user);
  }

  private void processUpdateProfile(User user, MultipartFile profileImage) throws IOException {
    if (profileImage == null) {
      log.debug("[Service] processUpdateProfile - LOGIC: 이미지 없음 확인");
      return;
    }

    // BinaryContent 생성
    BinaryContent content = new BinaryContent(
        profileImage.getOriginalFilename(), profileImage.getSize(), profileImage.getContentType());
    binaryContentRepository.save(content);
    binaryContentStorage.put(content.getId(), profileImage.getBytes());

    user.updateProfile(content);
    log.info("[Service] processUpdateProfile - LOGIC: binaryContent 생성");
  }
}
