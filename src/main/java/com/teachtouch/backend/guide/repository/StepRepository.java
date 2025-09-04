package com.teachtouch.backend.guide.repository;

import com.teachtouch.backend.guide.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepository extends JpaRepository<Step,Long> {
}
