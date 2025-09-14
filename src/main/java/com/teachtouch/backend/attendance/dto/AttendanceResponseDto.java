package com.teachtouch.backend.attendance.dto;

import com.teachtouch.backend.attendance.entity.Attendance;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceResponseDto {
    private final Long id;
    private final LocalDate attendanceDate;

    public AttendanceResponseDto(Attendance attendance) {
        this.id = attendance.getId();
        this.attendanceDate = attendance.getAttendanceDate();
    }

}
