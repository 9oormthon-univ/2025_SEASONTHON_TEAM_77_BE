package com.teachtouch.backend.guide.repository;

import com.teachtouch.backend.guide.entity.Step;
import com.teachtouch.backend.guide.entity.StepProgress;
import com.teachtouch.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StepProgressRepository extends JpaRepository<StepProgress, Long> {

    Optional<StepProgress> findByUserAndStep(User user, Step step);

    List<StepProgress> findByUser(User user);

    List<StepProgress> findByUserAndStep_Guide_IdAndCompletedTrue(User user, Long guideId);

}
