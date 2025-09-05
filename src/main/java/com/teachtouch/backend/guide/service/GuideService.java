package com.teachtouch.backend.guide.service;

import com.teachtouch.backend.guide.dto.GuideRequestDTO;
import com.teachtouch.backend.guide.entity.Guide;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GuideService {
    Guide upsertGuide(GuideRequestDTO dto);
    List<Guide> findAll();
    Optional<Guide> findById(Long id);
    List<Guide> searchByKeyword(String keyword);
    void markStepAsCompleted(Long stepId, Long userId);
    List<String> getCompletedStepCodes(Long userId, Long guideId);

    // 추가
    boolean isGuideCompleted(Long userId, Long guideId);
    Map<Long, Boolean> areGuidesCompleted(Long userId, List<Long> guideIds);
    void markGuideAsCompleted(Long guideId, Long userId); // 추후 전용 API용
    void deleteGuide(Long guideId);
}
