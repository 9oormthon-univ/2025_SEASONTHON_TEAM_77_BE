package com.teachtouch.backend.retouch.repository;

import com.teachtouch.backend.retouch.entity.TestProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestProgressRepository extends JpaRepository<TestProgress, Long> {
    Optional<TestProgress> findByUserIdAndTestId(Long userId, Long testId);
    List<TestProgress> findByUserId(Long userId);
    List<TestProgress> findByUserIdAndIsCompleted(Long userId, boolean isCompleted);
}
