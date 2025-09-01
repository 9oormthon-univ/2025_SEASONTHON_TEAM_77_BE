package com.teachtouch.backend.guide.controller;

import com.teachtouch.backend.guide.dto.GuideRequestDTO;
import com.teachtouch.backend.guide.dto.GuideResponseDTO;
import com.teachtouch.backend.guide.entity.Guide;
import com.teachtouch.backend.guide.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @PostMapping
    public ResponseEntity<GuideResponseDTO>upsertGuide(@RequestBody GuideRequestDTO dto){
        Guide saved = guideService.upsertGuide(dto);
        return ResponseEntity.ok(GuideResponseDTO.fromEntity(saved));
    }
}
