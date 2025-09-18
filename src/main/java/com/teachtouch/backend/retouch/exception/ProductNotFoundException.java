package com.teachtouch.backend.retouch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String name) {
        super("상품을 찾을 수 없습니다: " + name);
    }

    public ProductNotFoundException(Long id) {
        super("상품을 찾을 수 없습니다: " + id);
    }
}
