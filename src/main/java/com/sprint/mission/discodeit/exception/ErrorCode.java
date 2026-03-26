package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // User
  USER_NOT_FOUND("사용자를 찾을 수 없습니다"),
  DUPLICATE_USER("중복된 user입니다"),
  DUPLICATE_USERNAME("중복된 username입니다"),
  DUPLICATE_EMAIL("중복된 email입니다"),

  // UserStatus
  USERSTATUS_NOT_FOUND("userStatus를 찾을 수 없습니다"),
  USERSTATUS_ALREADY_EXISTS("이미 존재하는 userStatus입니다"),

  // Channel
  CHANNEL_NOT_FOUND("채널을 찾을 수 없습니다"),
  USER_ALREADY_IN_CHANNEL("이미 참가한 참가자입니다"),
  USER_NOT_IN_CHANNEL("채널에 참가하지 않은 유저입니다"),
  DUPLICATE_TITLE("중복된 채널명 입니다"),
  PRIVATE_CHANNEL_NOT_EDITABLE("Private 채널은 수정할 수 없습니다"),

  // BinaryContent
  BINARYCONTENT_NOT_FOUND("존재하지 않는 binaryContent입니다"),

  // Message
  MESSAGE_NOT_FOUND("존재하지 않는 메시지입니다"),

  // ReadStatus
  READSTATUS_ALREADY_EXISTS("이미 존재하는 readStatus입니다"),
  READSTATUS_NOT_FOUND("존재하지 않는 readStatus입니다");


  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String getCode() {
    return this.name();
  }
}
