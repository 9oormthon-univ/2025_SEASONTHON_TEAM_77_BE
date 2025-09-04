package com.teachtouch.backend.user.service;

import com.teachtouch.backend.auth.dto.TokenRefreshRequestDto;
import com.teachtouch.backend.auth.dto.TokenRefreshResponseDto;
import com.teachtouch.backend.user.dto.UserLoginRequestDto;
import com.teachtouch.backend.user.dto.UserLoginResponseDto;
import com.teachtouch.backend.user.dto.UserSignupRequestDto;

public interface UserService {

    void signup(UserSignupRequestDto requestDto);
    UserLoginResponseDto login(UserLoginRequestDto requestDto);

    void logout(String loginId);

    void delete(String loginId);

    String getLoginIdFromToken(String token);

    TokenRefreshResponseDto reissueAccessToken(TokenRefreshRequestDto requestDto);

    boolean checkLoginIdDuplicate(String loginId);

    Long getUserIdByLoginId(String loginId);
}