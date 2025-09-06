package com.teachtouch.backend.user.dto;

import lombok.Getter;

@Getter
public class UserLoginRequestDto {
    private String loginId;
    private String password;
}