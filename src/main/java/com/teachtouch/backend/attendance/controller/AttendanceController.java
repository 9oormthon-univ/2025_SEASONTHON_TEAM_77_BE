package com.teachtouch.backend.attendance.controller;

import com.teachtouch.backend.attendance.dto.AttendanceResponseDto;
import com.teachtouch.backend.attendance.dto.WeeklyAttendanceResponseDto;
import com.teachtouch.backend.attendance.service.AttendanceService;
import com.teachtouch.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public ResponseEntity<Void> checkIn(@AuthenticationPrincipal CustomUserDetails userDetails) {
        attendanceService.checkIn(userDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<AttendanceResponseDto>> getAttendanceHistory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AttendanceResponseDto> history = attendanceService.getAttendanceHistory(userDetails.getUser().getId());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/weekly-status")
    public ResponseEntity<WeeklyAttendanceResponseDto> getWeeklyAttendanceStatus(@AuthenticationPrincipal CustomUserDetails userDetails) {
        WeeklyAttendanceResponseDto responseDto = attendanceService.getWeeklyAttendanceStatus(userDetails.getUser().getId());
        return ResponseEntity.ok(responseDto);
    }

}
