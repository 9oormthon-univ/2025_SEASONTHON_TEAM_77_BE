package com.teachtouch.backend.attendance.repository;

import com.teachtouch.backend.attendance.entity.Attendance;
import com.teachtouch.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByUserAndAttendanceDate(User user, LocalDate date);
    List<Attendance> findAllByUserOrderByAttendanceDateDesc(User user);
    List<Attendance> findAllByUserAndAttendanceDateBetween(User user, LocalDate startOfWeek, LocalDate endOfWeek);
}
