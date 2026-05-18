package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.debug("유저 role 변경 요청 - userId={}, newRole={}", request.userId(), request.newRole());

    User findUser = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException());

    findUser.updateRole(request.newRole());
    log.info("유저 role 변경 성공 - userId={}, newRole={}", request.userId(), request.newRole());
    return userMapper.toDto(findUser);
  }
}
