package com.teachtouch.backend.retouch.repository;

import com.teachtouch.backend.retouch.entity.SolveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolveHistoryRepository extends JpaRepository<SolveHistory, Long> {
    List<SolveHistory> findByUserIdAndTestId(Long userId, Long testId);
    List<SolveHistory> findByUserId(Long userId);
}