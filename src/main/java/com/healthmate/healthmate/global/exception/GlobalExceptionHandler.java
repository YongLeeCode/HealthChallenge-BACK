package com.healthmate.healthmate.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(String code, String message) {}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String code = ex.getMessage() != null ? ex.getMessage() : "BAD_REQUEST";
        if ("EMAIL_DUPLICATE".equals(code)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(code, "이미 사용중인 이메일입니다."));
        }
        if ("NICKNAME_DUPLICATE".equals(code)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(code, "이미 사용중인 닉네임입니다."));
        }
        return ResponseEntity.badRequest().body(new ErrorResponse(code, "잘못된 요청입니다."));
    }
}


