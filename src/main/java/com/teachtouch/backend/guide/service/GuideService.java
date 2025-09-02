package com.teachtouch.backend.guide.service;

import com.teachtouch.backend.guide.dto.GuideRequestDTO;
import com.teachtouch.backend.guide.entity.Guide;

public interface GuideService {
    Guide upsertGuide(GuideRequestDTO dto);
}
