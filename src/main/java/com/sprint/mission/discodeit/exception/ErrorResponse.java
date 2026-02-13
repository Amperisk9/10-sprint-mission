package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private String message;
    private MethodArgumentNotValidException exception;
    public ErrorResponse(MethodArgumentNotValidException e) {
        this.exception = e;
    }

    public static class FieldError {
        private final String fieldErr;


        private FieldError(String fieldErr) {
            this.fieldErr = fieldErr;
        }


    }

}