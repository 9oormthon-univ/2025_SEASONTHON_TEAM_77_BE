package com.teachtouch.backend.retouch.controller;


import com.teachtouch.backend.retouch.dto.*;
import com.teachtouch.backend.retouch.service.RetouchService;
import com.teachtouch.backend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/retouch")
@RequiredArgsConstructor
public class RetouchController {

    private final RetouchService retouchService;
    private final UserService userService;

    @GetMapping("/test/all")
    public ResponseEntity<List<GetAllTestDto>> getAllTests() {
        List<GetAllTestDto> tests = retouchService.getTestList();
        return ResponseEntity.ok(tests);
    }


    @PostMapping("/test/add")
    public ResponseEntity<TestDto> addTest(@RequestBody CreateTestDto createTestDto) {
        TestDto created = retouchService.addTest(createTestDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<TestDto> getTestById(@PathVariable Long id) {
        TestDto test = retouchService.getTestById(id);
        return ResponseEntity.ok(test);
    }

    @PatchMapping("/progress")
    public ResponseEntity<String> saveProgress(@RequestBody TestProgressDto progressDto, HttpServletRequest request) {
        String LoginId = userService.getLoginIdFromToken(request.getHeader("Authorization"));
        Long userId = userService.getUserIdByLoginId(LoginId);
        retouchService.saveTestProgress(userId, progressDto);
        return ResponseEntity.ok("진행 상태가 저장되었습니다.");
    }

    @GetMapping("/progress/{testId}")
    public ResponseEntity<TestProgressResponseDto> getProgress(@PathVariable Long testId, HttpServletRequest request) {
        String LoginId = userService.getLoginIdFromToken(request.getHeader("Authorization"));
        Long userId = userService.getUserIdByLoginId(LoginId);
        TestProgressResponseDto progress = retouchService.getTestProgress(userId, testId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/progress/all")
    public ResponseEntity<List<TestProgressResponseDto>> getAllProgress(HttpServletRequest request) {
        String LoginId = userService.getLoginIdFromToken(request.getHeader("Authorization"));
        Long userId = userService.getUserIdByLoginId(LoginId);
        List<TestProgressResponseDto> progressList = retouchService.getAllTestProgress(userId);
        return ResponseEntity.ok(progressList);
    }

    @PostMapping("/submit")
    public ResponseEntity<TestResultDto> submitTest(@RequestBody TestSubmitDto submitDto, HttpServletRequest request) {
        String LoginId = userService.getLoginIdFromToken(request.getHeader("Authorization"));
        Long userId = userService.getUserIdByLoginId(LoginId);
        TestResultDto result = retouchService.submitTest(userId, submitDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/wrong")
    public ResponseEntity<List<WrongTestDto>> getWrongTests(HttpServletRequest request) {
        String LoginId = userService.getLoginIdFromToken(request.getHeader("Authorization"));
        Long userId = userService.getUserIdByLoginId(LoginId);
        List<WrongTestDto> wrongTests = retouchService.getWrongTests(userId);
        return ResponseEntity.ok(wrongTests);
    }

}
