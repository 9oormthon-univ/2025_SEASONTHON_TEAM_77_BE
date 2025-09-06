package com.teachtouch.backend.retouch.repository;

import com.teachtouch.backend.retouch.entity.SolveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolveHistoryRepository extends JpaRepository<SolveHistory, Long> {
    List<SolveHistory> findByUserIdAndTestId(Long userId, Long testId);
    List<SolveHistory> findByUserIdOrderByCreatedDateDesc(Long userId);
}