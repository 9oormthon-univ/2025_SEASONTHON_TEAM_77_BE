package com.teachtouch.backend.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckInResponseDto {
    private boolean success;
    private String message;

    public static CheckInResponseDto success(String message) {
        return new CheckInResponseDto(true, message);
    }

    public static CheckInResponseDto alreadyCheckIn(String message) {
        return new CheckInResponseDto(true, message);
    }
}
