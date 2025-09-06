package com.teachtouch.backend.user.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserSignupRequestDto {
    private String loginId;
    private String password;
    private String username;
    private String gender;
    private LocalDate birthdate;
}