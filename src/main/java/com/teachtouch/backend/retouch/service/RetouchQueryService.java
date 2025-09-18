package com.teachtouch.backend.retouch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachtouch.backend.retouch.dto.GetAllTestDto;
import com.teachtouch.backend.retouch.dto.TestDto;
import com.teachtouch.backend.retouch.dto.TestProgressDto;
import com.teachtouch.backend.retouch.dto.TestProgressResponseDto;
import com.teachtouch.backend.retouch.dto.WrongTestDto;
import com.teachtouch.backend.retouch.entity.SolveHistory;
import com.teachtouch.backend.retouch.entity.Test;
import com.teachtouch.backend.retouch.entity.TestProgress;
import com.teachtouch.backend.retouch.exception.TestNotFoundException;
import com.teachtouch.backend.retouch.repository.SolveHistoryRepository;
import com.teachtouch.backend.retouch.repository.TestProgressRepository;
import com.teachtouch.backend.retouch.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetouchQueryService {

    private final TestRepository testRepository;
    private final TestProgressRepository testProgressRepository;
    private final SolveHistoryRepository solveHistoryRepository;
    private final ObjectMapper objectMapper;

    public List<GetAllTestDto> getTestList() {
        log.info("모든 테스트 조회");
        List<GetAllTestDto> tests = testRepository.findAll()
                .stream()
                .map(GetAllTestDto::new)
                .collect(Collectors.toList());
        if (tests.isEmpty()) {
            log.warn("테스트를 찾을 수 없음");
            throw new TestNotFoundException("테스트가 존재하지 않습니다");
        }
        return tests;
    }

    public TestDto getTestById(Long id) {
        log.info("ID로 테스트 조회: {}", id);
        Test test = testRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("ID에 해당하는 테스트를 찾을 수 없음: {}", id);
                    return new TestNotFoundException(id);
                });
        log.info("테스트 '{}' (ID: {}) 조회 성공", test.getTitle(), id);
        return new TestDto(test);
    }

    public TestProgressResponseDto getTestProgress(Long userId, Long testId) {
        log.info("사용자 ID {}의 테스트 ID {} 진행 상황 조회", userId, testId);
        return testProgressRepository
                .findByUserIdAndTestId(userId, testId)
                .map(this::convertToProgressResponseDto)
                .orElse(null);
    }

    public List<TestProgressResponseDto> getAllTestProgress(Long userId) {
        log.info("사용자 ID {}의 모든 테스트 진행 상황 조회", userId);
        List<TestProgress> progressList = testProgressRepository.findByUserId(userId);
        return progressList.stream()
                .map(this::convertToProgressResponseDto)
                .collect(Collectors.toList());
    }

    public List<WrongTestDto> getWrongTests(Long userId) {
        log.info("사용자 ID {}의 틀린 문제 목록 조회", userId);
        List<SolveHistory> solveHistories = solveHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId);

        Map<Long, SolveHistory> lastHistories = new HashMap<>();
        for (SolveHistory history : solveHistories) {
            lastHistories.putIfAbsent(history.getTest().getId(), history);
        }

        return lastHistories.values().stream()
                .filter(solveHistory -> !solveHistory.isTrue())
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
                .map(this::convertToWrongTestDto)
                .collect(Collectors.toList());
    }

    private TestProgressResponseDto convertToProgressResponseDto(TestProgress progress) {
        TestProgressResponseDto dto = new TestProgressResponseDto();
        dto.setProgressId(progress.getId());
        dto.setTestId(progress.getTest().getId());
        dto.setTestTitle(progress.getTest().getTitle());
        dto.setCurrentStep(progress.getCurrentStep());
        dto.setElapsedTime(progress.getElapsedTime());
        dto.setCompleted(progress.isCompleted());
        dto.setCreatedDate(progress.getCreatedDate());
        dto.setUpdatedDate(progress.getUpdatedDate());

        if (progress.getSelectedProducts() != null && !progress.getSelectedProducts().isEmpty()) {
            try {
                List<TestProgressDto.SelectedProductDto> products = objectMapper.readValue(
                        progress.getSelectedProducts(),
                        objectMapper.getTypeFactory().constructCollectionType(
                                List.class, TestProgressDto.SelectedProductDto.class
                        )
                );
                dto.setSelectedProducts(products);
            } catch (Exception e) {
                log.error("진행 ID {}의 선택된 상품 JSON 파싱 실패", progress.getId(), e);
                dto.setSelectedProducts(new ArrayList<>());
            }
        } else {
            dto.setSelectedProducts(new ArrayList<>());
        }
        return dto;
    }

    private WrongTestDto convertToWrongTestDto(SolveHistory solveHistory) {
        WrongTestDto wrongTestDto = new WrongTestDto();
        wrongTestDto.setTestId(solveHistory.getTest().getId());
        wrongTestDto.setTestTitle(solveHistory.getTest().getTitle());
        wrongTestDto.setSolveHistoryId(solveHistory.getId());
        wrongTestDto.setTestDate(solveHistory.getCreatedDate());
        return wrongTestDto;
    }
}
