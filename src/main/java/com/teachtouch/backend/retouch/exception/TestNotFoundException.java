package com.teachtouch.backend.retouch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TestNotFoundException extends RuntimeException {
    public TestNotFoundException(Long id) {
        super("테스트를 찾을 수 없습니다: " + id);
    }

    public TestNotFoundException(String message) {
        super(message);
    }
}
