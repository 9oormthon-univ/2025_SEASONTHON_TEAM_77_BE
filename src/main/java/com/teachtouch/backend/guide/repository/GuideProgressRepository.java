package com.teachtouch.backend.guide.repository;

import com.teachtouch.backend.guide.entity.Guide;
import com.teachtouch.backend.guide.entity.GuideProgress;
import com.teachtouch.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuideProgressRepository extends JpaRepository<GuideProgress, Long> {
    Optional<GuideProgress> findByUserAndGuide(User user, Guide guide);

    List<GuideProgress> findByUserAndGuide_IdIn(User user, List<Long> guideIds);
}

