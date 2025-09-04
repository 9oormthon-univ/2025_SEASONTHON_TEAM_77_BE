package com.teachtouch.backend.guide.controller;

import com.teachtouch.backend.global.security.CustomUserDetails;
import com.teachtouch.backend.guide.dto.GuideRequestDTO;
import com.teachtouch.backend.guide.dto.GuideResponseDTO;
import com.teachtouch.backend.guide.entity.Guide;
import com.teachtouch.backend.guide.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @PostMapping
    public ResponseEntity<GuideResponseDTO> upsertGuide(@RequestBody GuideRequestDTO dto) {
        Guide saved = guideService.upsertGuide(dto);
        return ResponseEntity.ok(GuideResponseDTO.fromEntity(saved, true));
    }
    /* 전체 조회 : 가이드 list 조회 */
    @GetMapping
    public ResponseEntity<List<GuideResponseDTO>> getAllGuides() {
        List<Guide> guides = guideService.findAll();
        return ResponseEntity.ok(
                guides.stream()
                        .map(g -> GuideResponseDTO.fromEntity(g, false))
                        .toList()
        );
    }

    /* 단일 조회 : 각 가이드 본문 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> getGuideDetail(@PathVariable Long id) {
        Guide guide = guideService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가이드: " + id));
        return ResponseEntity.ok(GuideResponseDTO.fromEntity(guide, true));
    }

    /* 가이드를 카테고리를 통해 조회 */
    @GetMapping("/search")
    public ResponseEntity<List<GuideResponseDTO>> searchGuides(@RequestParam String keyword) {
        List<Guide> guides = guideService.searchByKeyword(keyword);
        return ResponseEntity.ok(
                guides.stream()
                        .map(g -> GuideResponseDTO.fromEntity(g, false))
                        .toList()
        );
    }

    /* 가이드의 각 step 완수 완료 저장 */
    @PostMapping("/steps/{stepId}/complete")
    public ResponseEntity<Void> completeStep(@PathVariable Long stepId,
                                             @AuthenticationPrincipal UserDetails user) {
        Long userId = ((CustomUserDetails) user).getUser().getId();
        guideService.markStepAsCompleted(stepId, userId);
        return ResponseEntity.ok().build();
    }


    /* 해당 가이드의 완수된 step 조회 */
    @GetMapping("/{guideId}/steps/progress")
    public ResponseEntity<List<String>> getUserCompletedSteps(
            @PathVariable Long guideId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getId();
        List<String> completed = guideService.getCompletedStepCodes(userId, guideId);
        return ResponseEntity.ok(completed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "가이드가 성공적으로 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }




}
