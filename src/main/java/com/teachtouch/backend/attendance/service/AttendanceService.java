package com.teachtouch.backend.attendance.service;

import com.teachtouch.backend.attendance.dto.AttendanceResponseDto;
import com.teachtouch.backend.attendance.dto.WeeklyAttendanceResponseDto;

import java.util.List;

public interface AttendanceService {
    void checkIn(Long userId);
    List<AttendanceResponseDto> getAttendanceHistory(Long userId);
    WeeklyAttendanceResponseDto getWeeklyAttendanceStatus(Long userId);
}
