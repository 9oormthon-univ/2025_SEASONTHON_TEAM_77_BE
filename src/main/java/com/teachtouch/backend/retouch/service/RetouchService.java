package com.teachtouch.backend.retouch.service;

import com.teachtouch.backend.retouch.dto.*;

import java.util.List;
import java.util.Optional;

public interface RetouchService {
    List<GetAllTestDto> getTestList();

    TestDto getTestById(Long id);

    TestDto addTest(CreateTestDto createTestDto);

    void saveTestProgress(Long userId, TestProgressDto progressDto);

    TestProgressResponseDto getTestProgress(Long userId, Long testId);

    List<TestProgressResponseDto> getAllTestProgress(Long userId);

    TestResultDto submitTest(Long userId, TestSubmitDto submitDto);
}
