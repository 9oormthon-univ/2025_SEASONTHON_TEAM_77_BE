package com.teachtouch.backend.guide.repository;

import com.teachtouch.backend.guide.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StepRepository extends JpaRepository<Step,Long> {
    Optional<Step> findByStepCode(String stepCode);

}
