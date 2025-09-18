package com.teachtouch.backend.retouch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.entity.ProductOption;
import com.teachtouch.backend.product.repository.ProductRepository;
import com.teachtouch.backend.retouch.dto.*;
import com.teachtouch.backend.retouch.entity.*;
import com.teachtouch.backend.retouch.exception.ProductNotFoundException;
import com.teachtouch.backend.retouch.exception.TestNotFoundException;
import com.teachtouch.backend.retouch.exception.UserNotFoundException;
import com.teachtouch.backend.retouch.repository.*;
import com.teachtouch.backend.user.entity.User;
import com.teachtouch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RetouchCommandService {

    private final TestRepository testRepository;
    private final ProductRepository productRepository;
    private final TestProgressRepository testProgressRepository;
    private final UserRepository userRepository;
    private final SolveHistoryRepository solveHistoryRepository;
    private final SolveHistoryProductRepository solveHistoryProductRepository;
    private final TestGrader testGrader;
    private final ObjectMapper objectMapper;

    public TestDto addTest(CreateTestDto createTestDto) {
        log.info("새로운 테스트 추가, 제목: {}", createTestDto.getTitle());
        Test test = new Test();
        test.setTitle(createTestDto.getTitle());
        test.setDescription(createTestDto.getDescription());
        test.setTimeLimit(createTestDto.getTimeLimit());
        test.setDifficulty(createTestDto.getDifficulty());

        TestOrder testOrder = new TestOrder();
        testOrder.setName(createTestDto.getTestOrderName());
        testOrder.setTest(test);

        List<TestOrderProduct> testOrderProducts = createTestDto.getProducts().stream()
                .map(productDto -> {
                    Product product = productRepository.findByName(productDto.getName())
                            .orElseThrow(() -> new ProductNotFoundException(productDto.getName()));

                    TestOrderProduct testOrderProduct = TestOrderProduct.builder()
                            .testOrder(testOrder)
                            .product(product)
                            .quantity(productDto.getQuantity())
                            .build();

                    if (productDto.getProductOptions() != null && !productDto.getProductOptions().isEmpty()) {
                        List<ProductOption> productOptions = productDto.getProductOptions().stream()
                                .map(optionDto -> ProductOption.builder()
                                        .optionName(optionDto.getOptionName())
                                        .optionValue(optionDto.getOptionValue())
                                        .product(product)
                                        .testOrderProduct(testOrderProduct)
                                        .build())
                                .collect(Collectors.toList());
                        testOrderProduct.setProductOptions(productOptions);
                    }
                    return testOrderProduct;
                })
                .collect(Collectors.toList());

        testOrder.setTestOrderProducts(testOrderProducts);
        test.setTestOrder(testOrder);

        Test savedTest = testRepository.save(test);
        log.info("새로운 테스트 저장 성공, ID: {}", savedTest.getId());
        return new TestDto(savedTest);
    }

    public void saveTestProgress(Long userId, TestProgressDto progressDto) {
        log.info("사용자 ID {}의 테스트 ID {} 진행 상황 저장", userId, progressDto.getTestId());
        TestProgress progress = testProgressRepository
                .findByUserIdAndTestId(userId, progressDto.getTestId())
                .orElseGet(() -> {
                    log.info("사용자 ID {}의 테스트 ID {}에 대한 새로운 진행 상황 생성", userId, progressDto.getTestId());
                    TestProgress newProgress = new TestProgress();
                    newProgress.setUser(userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException(userId)));
                    newProgress.setTest(testRepository.findById(progressDto.getTestId())
                            .orElseThrow(() -> new TestNotFoundException(progressDto.getTestId())));
                    newProgress.setCompleted(false);
                    return newProgress;
                });

        progress.setCurrentStep(progressDto.getCurrentStep());
        progress.setElapsedTime(progressDto.getElapsedTime());

        try {
            String productsJson = objectMapper.writeValueAsString(progressDto.getSelectedProducts());
            progress.setSelectedProducts(productsJson);
        } catch (Exception e) {
            log.error("사용자 ID {}의 선택된 상품 JSON 직렬화 실패", userId, e);
            throw new RuntimeException("상품 정보 저장에 실패했습니다.", e);
        }

        if ("COMPLETE".equals(progressDto.getCurrentStep())) {
            progress.setCompleted(true);
        }

        testProgressRepository.save(progress);
        log.info("사용자 ID {}의 테스트 진행 상황 저장 성공", userId);
    }

    public TestResultDto submitTest(Long userId, TestSubmitDto submitDto) {
        log.info("사용자 ID {}의 테스트 ID {} 제출", userId, submitDto.getTestId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Test test = testRepository.findById(submitDto.getTestId())
                .orElseThrow(() -> new TestNotFoundException(submitDto.getTestId()));

        TestResultDto result = testGrader.grade(test, submitDto);
        log.info("테스트 ID {} 채점 완료. 정답 여부: {}", submitDto.getTestId(), result.isCorrect());

        saveSolveHistory(user, test, submitDto, result);
        updateProgressToComplete(user.getId(), test.getId(), submitDto.getDuration());

        return result;
    }

    private void saveSolveHistory(User user, Test test, TestSubmitDto submitDto, TestResultDto result) {
        SolveHistory solveHistory = new SolveHistory();
        solveHistory.setUser(user);
        solveHistory.setTest(test);
        solveHistory.setTrue(result.isCorrect());
        solveHistory.setDuration(submitDto.getDuration());
        SolveHistory savedSolveHistory = solveHistoryRepository.save(solveHistory);

        List<SolveHistoryProduct> solveHistoryProducts = submitDto.getSubmittedProducts().stream()
                .map(dto -> {
                    Product product = productRepository.findByName(dto.getProductName())
                            .orElseThrow(() -> new ProductNotFoundException(dto.getProductName()));
                    return SolveHistoryProduct.builder()
                            .solveHistory(savedSolveHistory)
                            .product(product)
                            .quantity(dto.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        savedSolveHistory.setSolveHistoryProducts(solveHistoryProducts);
        solveHistoryProductRepository.saveAll(solveHistoryProducts);
        log.info("사용자 ID {}의 테스트 ID {} 풀이 기록 저장", user.getId(), test.getId());
    }

    private void updateProgressToComplete(Long userId, Long testId, int duration) {
        testProgressRepository.findByUserIdAndTestId(userId, testId)
                .ifPresent(progress -> {
                    progress.setCurrentStep("완료");
                    progress.setElapsedTime(duration);
                    progress.setCompleted(true);
                    testProgressRepository.save(progress);
                    log.info("사용자 ID {}의 테스트 ID {} 진행 상황을 '완료'로 업데이트", userId, testId);
                });
    }
}
