package com.sprint.mission.discodeit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(Exception e) {
//        ErrorResponse response = new ErrorResponse(33, e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDTOException(MethodArgumentNotValidException e) {
        ErrorResponse response = new ErrorResponse(e);
        System.out.printf("메시지: %s%n", e.getMessage());
        System.out.printf("body: %s%n", e.getBody());
        System.out.printf("DetailMessageArguments: %s%n", e.getDetailMessageArguments());
        System.out.printf("getParameter: %s%n", e.getParameter());
        System.out.printf("getStatusCode: %s%n", e.getStatusCode());
        System.out.printf("getCause: %s%n", e.getCause());
        System.out.printf("getLocalizedMessage: %s%n", e.getLocalizedMessage());
        System.out.printf("getBindingResult: %s%n", e.getBindingResult());
        e.getBindingResult().

        return ResponseEntity.status(402).body(response);
    }
}
