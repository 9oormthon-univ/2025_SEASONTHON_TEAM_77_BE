package com.teachtouch.backend.user.controller;


import com.teachtouch.backend.auth.dto.TokenRefreshRequestDto;
import com.teachtouch.backend.auth.dto.TokenRefreshResponseDto;
import com.teachtouch.backend.user.dto.UserLoginRequestDto;
import com.teachtouch.backend.user.dto.UserLoginResponseDto;
import com.teachtouch.backend.user.dto.UserSignupRequestDto;
import com.teachtouch.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenRefreshResponseDto> reissue(@RequestBody TokenRefreshRequestDto dto) {
        return ResponseEntity.ok(userService.reissueAccessToken(dto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String token) {
        String loginId = userService.getLoginIdFromToken(token);
        userService.delete(loginId);
        return ResponseEntity.ok("탈퇴 완료");
    }
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkLoginIdDuplicate(@RequestParam String loginId) {
        boolean isDuplicate = userService.checkLoginIdDuplicate(loginId);
        return ResponseEntity.ok(isDuplicate);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String loginId = userService.getLoginIdFromToken(token);
        userService.logout(loginId);
        return ResponseEntity.ok("로그아웃 완료");
    }




}
