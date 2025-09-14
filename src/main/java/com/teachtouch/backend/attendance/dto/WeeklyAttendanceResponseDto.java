package com.teachtouch.backend.attendance.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyAttendanceResponseDto {
    private final List<Boolean> attendance;

    public WeeklyAttendanceResponseDto(List<Boolean> attendance) {
        this.attendance = attendance;
    }
}
