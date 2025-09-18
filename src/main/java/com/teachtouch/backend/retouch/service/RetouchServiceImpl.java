package com.teachtouch.backend.retouch.service;

import com.teachtouch.backend.retouch.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetouchServiceImpl implements RetouchService {

    private final RetouchQueryService retouchQueryService;
    private final RetouchCommandService retouchCommandService;

    @Override
    public List<GetAllTestDto> getTestList() {
        return retouchQueryService.getTestList();
    }

    @Override
    public TestDto getTestById(Long id) {
        return retouchQueryService.getTestById(id);
    }

    @Override
    public TestDto addTest(CreateTestDto createTestDto) {
        return retouchCommandService.addTest(createTestDto);
    }

    @Override
    public void saveTestProgress(Long userId, TestProgressDto progressDto) {
        retouchCommandService.saveTestProgress(userId, progressDto);
    }

    @Override
    public TestProgressResponseDto getTestProgress(Long userId, Long testId) {
        return retouchQueryService.getTestProgress(userId, testId);
    }

    @Override
    public List<TestProgressResponseDto> getAllTestProgress(Long userId) {
        return retouchQueryService.getAllTestProgress(userId);
    }

    @Override
    public TestResultDto submitTest(Long userId, TestSubmitDto submitDto) {
        return retouchCommandService.submitTest(userId, submitDto);
    }

    @Override
    public List<WrongTestDto> getWrongTests(Long userId) {
        return retouchQueryService.getWrongTests(userId);
    }
}
