package com.teachtouch.backend.attendance.service;

import com.teachtouch.backend.attendance.dto.AttendanceResponseDto;
import com.teachtouch.backend.attendance.dto.CheckInResponseDto;
import com.teachtouch.backend.attendance.dto.WeeklyAttendanceResponseDto;
import com.teachtouch.backend.attendance.entity.Attendance;
import com.teachtouch.backend.attendance.repository.AttendanceRepository;
import com.teachtouch.backend.user.entity.User;
import com.teachtouch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Override
    public CheckInResponseDto checkIn(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        LocalDate today = LocalDate.now();
        if(attendanceRepository.existsByUserAndAttendanceDate(user, today)) {
            return CheckInResponseDto.alreadyCheckIn("이미 오늘 출석체크를 완료했습니다.");
        }

        Attendance attendance = Attendance.builder()
                .user(user)
                .attendanceDate(today)
                .build();
        attendanceRepository.save(attendance);

        return CheckInResponseDto.success("출석체크가 완료되었습니다.");
    }

    @Override
    public List<AttendanceResponseDto> getAttendanceHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        return attendanceRepository.findAllByUserOrderByAttendanceDateDesc(user).stream()
                .map(AttendanceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public WeeklyAttendanceResponseDto getWeeklyAttendanceStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<Attendance> weeklyAttendances = attendanceRepository
                .findAllByUserAndAttendanceDateBetween(user, startOfWeek, endOfWeek);

        Set<LocalDate> attendedDates = weeklyAttendances.stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        List<Boolean> attendanceStatus = new ArrayList<>();
        LocalDate currentDate = startOfWeek;
        while (!currentDate.isAfter(endOfWeek)) {
            attendanceStatus.add(attendedDates.contains(currentDate));
            currentDate = currentDate.plusDays(1);
        }

        return new WeeklyAttendanceResponseDto(attendanceStatus);
    }

}
