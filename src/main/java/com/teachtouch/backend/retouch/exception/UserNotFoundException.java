package com.teachtouch.backend.retouch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("유저를 찾을 수 없습니다: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
