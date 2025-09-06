package com.teachtouch.backend.user.dto;


import com.teachtouch.backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long id;
    private String loginId;
    private String username;
    private String gender;
    private LocalDate birthDate;
    private String profileImgUrl;

    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getGender(),
                user.getBirthDate(),
                user.getProfileImageUrl()
        );
    }
}

