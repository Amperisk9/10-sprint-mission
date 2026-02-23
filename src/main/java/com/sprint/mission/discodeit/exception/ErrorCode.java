package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // User
    USER_NOT_FOUND(404, "사용자를 찾을 수 없음"),
    USER_WRONG_PASSWORD(400, "계정명이나 패스워드가 다릅니다");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getCode() {
        return this.name();
    }
}
