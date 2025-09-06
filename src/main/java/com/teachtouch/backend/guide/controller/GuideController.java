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

import java.util.*;

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

    /* 가이드 전체 조회  */
    @GetMapping
    public ResponseEntity<List<GuideResponseDTO>> getAllGuides(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        List<Guide> guides = guideService.findAll();
        Map<Long, Boolean> completedMap;
        if (principal != null) {
            Long userId = principal.getUser().getId();
            List<Long> ids = guides.stream().map(Guide::getId).toList();
            completedMap = guideService.areGuidesCompleted(userId, ids);
        } else {
            completedMap = Collections.emptyMap();
        }

        final Map<Long, Boolean> finalCompletedMap = completedMap;

        List<GuideResponseDTO> response = guides.stream()
                .map(g -> {
                    boolean completed = finalCompletedMap.getOrDefault(g.getId(), false);
                    return GuideResponseDTO.fromEntity(g, false, completed);
                })
                .toList();


        return ResponseEntity.ok(response);
    }

    /* 가이드 단일 조회  user별 completed 포함 */
    @GetMapping("/{id}")
    public ResponseEntity<GuideResponseDTO> getGuideDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Guide guide = guideService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가이드: " + id));

        boolean completed = false;
        if (principal != null) {
            Long userId = principal.getUser().getId();
            completed = guideService.isGuideCompleted(userId, id);
        }

        return ResponseEntity.ok(GuideResponseDTO.fromEntity(guide, true, completed));
    }

    /* 가이드 검색 */
    @GetMapping("/search")
    public ResponseEntity<List<GuideResponseDTO>> searchGuides(@RequestParam String keyword) {
        List<Guide> guides = guideService.searchByKeyword(keyword);
        return ResponseEntity.ok(
                guides.stream()
                        .map(g -> GuideResponseDTO.fromEntity(g, false))
                        .toList()
        );
    }

//    /* Step 단위 완료 저장 */
//    @PostMapping("/steps/{stepId}/complete")
//    public ResponseEntity<Void> completeStep(@PathVariable Long stepId,
//                                             @AuthenticationPrincipal UserDetails user) {
//        Long userId = ((CustomUserDetails) user).getUser().getId();
//        guideService.markStepAsCompleted(stepId, userId);
//        return ResponseEntity.ok().build();
//    }


    //    /* Step 단위 완료 조회 */
//    @GetMapping("/{guideId}/steps/progress")
//    public ResponseEntity<List<String>> getUserCompletedSteps(
//            @PathVariable Long guideId,
//            @AuthenticationPrincipal CustomUserDetails user) {
//
//        Long userId = user.getUser().getId();
//        List<String> completed = guideService.getCompletedStepCodes(userId, guideId);
//        return ResponseEntity.ok(completed);
//    }

    /* Step 단위 완료 저장 -> 1-1. 1-2 방식으로 저장할 수 있게 수정*/
    @PostMapping("/{stepCode}/complete")
    public ResponseEntity<Void> completeStep(
            @PathVariable String stepCode,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUser().getId();
        guideService.markStepAsCompleted(stepCode, userId);
        return ResponseEntity.ok().build();
    }

    /* Step 단위 완료 조회 -> 특정 가이드 내 사용자가 완료한 stepCode 목록(1-1,1-2) 반환 */
    @GetMapping("/{guideId}/steps/progress")
    public ResponseEntity<List<String>> getCompletedSteps(
            @PathVariable Long guideId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUser().getId();
        List<String> completedStepCodes = guideService.getCompletedStepCodes(userId, guideId);
        return ResponseEntity.ok(completedStepCodes);
    }

    /* Guide 단위 완료 저장 */
    @PostMapping("/{guideId}/guides/complete")
    public ResponseEntity<Void> completeGuide(
            @PathVariable Long guideId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getId();
        guideService.markGuideAsCompleted(guideId, userId);
        return ResponseEntity.ok().build();
    }

    /* Guide 단위 완료 여부 조회 */
    @GetMapping("/{guideId}/progress")
    public ResponseEntity<Boolean> isGuideCompleted(
            @PathVariable Long guideId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUser().getId();
        boolean completed = guideService.isGuideCompleted(userId, guideId);
        return ResponseEntity.ok(completed);
    }

    /* 가이드 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "가이드가 성공적으로 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }
}
