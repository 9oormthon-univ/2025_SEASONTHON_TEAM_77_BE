package com.teachtouch.backend.user.service;

import com.teachtouch.backend.auth.dto.TokenRefreshRequestDto;
import com.teachtouch.backend.auth.dto.TokenRefreshResponseDto;
import com.teachtouch.backend.auth.entity.RefreshToken;
import com.teachtouch.backend.auth.repository.RefreshTokenRepository;
import com.teachtouch.backend.global.jwt.JwtProvider;
import com.teachtouch.backend.user.dto.UserLoginRequestDto;
import com.teachtouch.backend.user.dto.UserLoginResponseDto;
import com.teachtouch.backend.user.dto.UserSignupRequestDto;
import com.teachtouch.backend.user.entity.User;
import com.teachtouch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Override
    public void signup(UserSignupRequestDto requestDto) {
        if (userRepository.findByLoginId(requestDto.getLoginId()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (requestDto.getUsername() == null || requestDto.getUsername().isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        User user = User.builder()
                .loginId(requestDto.getLoginId())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .username(requestDto.getUsername())
                .gender(requestDto.getGender())
                .birthDate(requestDto.getBirthdate())
                .build();

        userRepository.save(user);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByLoginId(requestDto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getLoginId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getLoginId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .loginId(user.getLoginId())
                        .token(refreshToken)
                        .build()
        );

        return new UserLoginResponseDto(accessToken, refreshToken);
    }

    @Override
    public void logout(String loginId) {
        refreshTokenRepository.deleteById(loginId);
    }


    @Override
    public void delete(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    public String getLoginIdFromToken(String token) {
        return jwtProvider.getLoginIdFromToken(token.replace("Bearer ", ""));
    }

    @Override
    public TokenRefreshResponseDto reissueAccessToken(TokenRefreshRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String loginId = jwtProvider.getLoginIdFromToken(refreshToken);

        RefreshToken saved = refreshTokenRepository.findById(loginId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰을 찾을 수 없습니다."));

        if (!saved.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtProvider.generateAccessToken(loginId);
        return new TokenRefreshResponseDto(newAccessToken);
    }
}
