package com.sprint.mission.discodeit.service.basic;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  UserRepository userRepository;
  @Mock
  BinaryContentRepository binaryContentRepository;
  @Mock
  BinaryContentStorage binaryContentStorage;
  @Mock
  UserMapper mapper;

  @InjectMocks
  BasicUserService userService;

  @Nested
  class createUser {

    @Test
    void should_return_userDto_when_newUsername_and_newEmail_without_profile() throws IOException {
      // given
      String username = "A";
      String email = "A@gmail.com";
      String password = "AAA";
      UUID id = UUID.randomUUID();
      User user = new User(username, password, email);
      UserDto.UserCreateRequest request = new UserCreateRequest(username, password, email);

      given(userRepository.existsByUsername(anyString())).willReturn(false);
      given(userRepository.existsByEmail(anyString())).willReturn(false);
      given(userRepository.save(any(User.class))).willReturn(user);
      UserDto expectedDto = new UserDto(UUID.randomUUID(), username, email, null, true);
      given(mapper.toDto(any(User.class))).willReturn(expectedDto);

      // when
      UserDto actualDto = userService.createUser(request, null);

      // then
      assertEquals(expectedDto, actualDto);

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      then(userRepository).should().save(userCaptor.capture());
      User getUser = userCaptor.getValue();
      assertNotNull(getUser.getStatus());
    }
  }

//  @Test
//  void updateUser() {
//  }
//
//  @Test
//  void deleteUser() {
//  }
}