package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto createUser(UserDto.UserCreateRequest userReq, BinaryContentDto.BinaryContentCreateRequest profileReq) throws IOException;
    UserDto findUser(UUID uuid);
    UserDto findUserByUsername(String username);
    UserDto findUserByEmail(String mail);
    List<UserDto> findAllUsers();
    UserDto updateUser(UUID uuid, UserDto.UserUpdateRequest userReq, BinaryContentDto.BinaryContentCreateRequest profileReq) throws IOException;
    void deleteUser(UUID uuid);
}
