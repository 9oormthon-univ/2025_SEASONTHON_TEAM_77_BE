package com.teachtouch.backend.retouch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachtouch.backend.product.dto.CreateProductOptionDto;
import com.teachtouch.backend.product.dto.ProductOptionDto;
import com.teachtouch.backend.product.dto.ProductResponseDTO;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.entity.ProductOption;
import com.teachtouch.backend.product.repository.ProductRepository;
import com.teachtouch.backend.retouch.dto.*;
import com.teachtouch.backend.retouch.entity.*;
import com.teachtouch.backend.retouch.repository.*;
import com.teachtouch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RetouchServiceImpl implements RetouchService {

    private final TestRepository testRepository;
    private final ProductRepository productRepository;
    private final TestProgressRepository testProgressRepository;
    private final UserRepository userRepository;
    private final SolveHistoryRepository solveHistoryRepository;
    private final SolveHistoryProductRepository solveHistoryProductRepository;

    @Override
    public List<GetAllTestDto> getTestList() {
        List<GetAllTestDto> tests = testRepository.findAll()
                .stream()
                .map(GetAllTestDto::new)
                .collect(Collectors.toList());
        if(tests.isEmpty()){
            throw new IllegalArgumentException("테스트가 존재하지 않습니다");
        }
        return tests;
    }

    @Override
    public TestDto getTestById(Long id) {
        Test test =  testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));
        return new TestDto(test);
    }

    @Override
    public TestDto addTest(CreateTestDto createTestDto) {
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
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productDto.getName()));

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

        return new TestDto(savedTest);
    }

    @Override
    public void saveTestProgress(Long userId, TestProgressDto progressDto) {
        // 기존 진행상태 찾기 또는 새로 생성 (Upsert)
        TestProgress progress = testProgressRepository
                .findByUserIdAndTestId(userId, progressDto.getTestId())
                .orElse(new TestProgress());

        // 새 생성시 초기값 설정
        if (progress.getId() == null) {
            progress.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));
            progress.setTest(testRepository.findById(progressDto.getTestId())
                    .orElseThrow(() -> new IllegalArgumentException("테스트를 찾을 수 없습니다.")));
            progress.setCompleted(false);
        }

        // 공통 업데이트
        progress.setCurrentStep(progressDto.getCurrentStep());
        progress.setElapsedTime(progressDto.getElapsedTime());

        // 선택된 상품들을 JSON으로 변환하여 저장
        try {
            ObjectMapper mapper = new ObjectMapper();
            String productsJson = mapper.writeValueAsString(progressDto.getSelectedProducts());
            progress.setSelectedProducts(productsJson);
        } catch (Exception e) {
            throw new RuntimeException("상품 정보 저장 실패", e);
        }

        // 완료 상태 체크
        if ("COMPLETE".equals(progressDto.getCurrentStep())) {
            progress.setCompleted(true);
        }

        testProgressRepository.save(progress);
    }

    @Override
    public TestProgressResponseDto getTestProgress(Long userId, Long testId) {
        TestProgress progress = testProgressRepository
                .findByUserIdAndTestId(userId, testId)
                .orElse(null);

        if (progress == null) {
            return null;
        }

        return convertToProgressResponseDto(progress);
    }

    @Override
    public List<TestProgressResponseDto> getAllTestProgress(Long userId) {
        List<TestProgress> progressList = testProgressRepository.findByUserId(userId);
        return progressList.stream()
                .map(this::convertToProgressResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TestResultDto submitTest(Long userId, TestSubmitDto submitDto) {
        // 1. 테스트 정보 조회
        Test test = testRepository.findById(submitDto.getTestId())
                .orElseThrow(() -> new IllegalArgumentException("테스트를 찾을 수 없습니다."));

        // 2. 정답 데이터 추출
        List<TestOrderProduct> correctAnswers = test.getTestOrder().getTestOrderProducts();

        // 3. 채점 수행
        TestResultDto result = gradeTest(test, correctAnswers, submitDto);

        // 4. SolveHistory 저장
        saveSolveHistory(userId, test, submitDto, result);

        // 5. 진행 상태를 완료로 업데이트
        updateProgressToComplete(userId, submitDto.getTestId(), submitDto.getDuration());

        return result;
    }

    private TestResultDto gradeTest(Test test, List<TestOrderProduct> correctAnswers, TestSubmitDto submitDto) {
        List<TestResultDto.ProductComparisonDto> productResults = new ArrayList<>();
        int correctCount = 0;

        // 정답 상품들을 Map으로 변환 (상품ID -> TestOrderProduct)
        Map<Long, TestOrderProduct> correctProductMap = correctAnswers.stream()
                .collect(Collectors.toMap(
                        top -> top.getProduct().getId(),
                        top -> top
                ));

        // 제출한 상품들을 Map으로 변환 (상품ID -> SubmittedProductDto)
        Map<String, TestSubmitDto.SubmittedProductDto> submittedProductMap = submitDto.getSubmittedProducts().stream()
                .collect(Collectors.toMap(
                        TestSubmitDto.SubmittedProductDto::getProductName,
                        dto -> dto
                ));

        // 정답 상품들 체크
        for (TestOrderProduct correctItem : correctAnswers) {
            Long productId = correctItem.getProduct().getId();
            String productName = correctItem.getProduct().getName();
            int correctQuantity = correctItem.getQuantity();
            List<ProductOptionDto> productOptions = correctItem.getProductOptions().stream()
                    .map(option -> new ProductOptionDto(option.getOptionName(), option.getOptionValue()))
                    .collect(Collectors.toList());

            TestResultDto.ProductComparisonDto comparison = new TestResultDto.ProductComparisonDto();
            comparison.setProductName(productName);
            comparison.setCorrectQuantity(correctQuantity);
            comparison.setProductOptions(productOptions);

            TestSubmitDto.SubmittedProductDto submittedProduct = submittedProductMap.get(productId);

            if (submittedProduct == null) {
                // 상품이 제출되지 않음
                comparison.setSubmittedQuantity(0);
                comparison.setStatus("목록에서 빠짐");
                comparison.setCorrect(false);
            } else {
                int submittedQuantity = submittedProduct.getQuantity();
                comparison.setSubmittedQuantity(submittedQuantity);

                if (submittedQuantity != correctQuantity) {
                    // 수량 틀림
                    comparison.setStatus("수량 틀림");
                    comparison.setCorrect(false);
                } else {
                    // 수량은 맞으니 옵션 비교
                    boolean optionsCorrect = compareProductOptions(
                            correctItem.getProductOptions(),
                            submittedProduct.getProductOptions()
                    );

                    if (optionsCorrect) {
                        comparison.setStatus("정답");
                        comparison.setCorrect(true);
                        correctCount++;
                    } else {
                        comparison.setStatus("옵션 틀림");
                        comparison.setCorrect(false);
                    }
                }
            }

            productResults.add(comparison);
        }

        // 추가로 제출한 상품들 체크 (정답에 없는 상품)
        for (TestSubmitDto.SubmittedProductDto submitted : submitDto.getSubmittedProducts()) {
            if (!correctProductMap.containsKey(submitted.getProductName())) {
                TestResultDto.ProductComparisonDto comparison = new TestResultDto.ProductComparisonDto();
                comparison.setProductName(submitted.getProductName());
                comparison.setCorrectQuantity(0);
                comparison.setSubmittedQuantity(submitted.getQuantity());
                comparison.setStatus("추가 상품");
                comparison.setCorrect(false);
                if (submitted.getProductOptions() != null && !submitted.getProductOptions().isEmpty()) {
                    List<ProductOptionDto> submittedOptions = submitted.getProductOptions().stream()
                            .map(option -> new ProductOptionDto(option.getOptionName(), option.getOptionValue()))
                            .collect(Collectors.toList());
                    comparison.setProductOptions(submittedOptions);
                }
                productResults.add(comparison);
            }
        }

        // 완전 정답 여부 체크
        boolean isCorrect = correctCount == correctAnswers.size() &&
                submittedProductMap.size() == correctProductMap.size();

        // 피드백 생성
        String feedback = generateFeedback(isCorrect, correctCount, correctAnswers.size());

        // 테스트 요약 정보 생성
        TestResultDto.TestSummaryDto testSummary = new TestResultDto.TestSummaryDto();
        testSummary.setTestTitle(test.getTitle());
        testSummary.setCorrectAnswer(test.getTestOrder().getName());
        testSummary.setSubmittedAnswer(generateSubmittedAnswer(submitDto.getSubmittedProducts()));

        // 결과 DTO 생성
        TestResultDto result = new TestResultDto();
        result.setCorrect(isCorrect);
        result.setDuration(submitDto.getDuration());
        result.setFeedback(feedback);
        result.setProductResults(productResults);
        result.setTestSummary(testSummary);

        return result;
    }

    private boolean compareProductOptions(List<ProductOption> correctOptions,
                                          List<TestSubmitDto.SubmittedOptionDto> submittedOptions) {
        // null 체크
        if (correctOptions == null) correctOptions = new ArrayList<>();
        if (submittedOptions == null) submittedOptions = new ArrayList<>();

        // 개수가 다르면 틀림
        if (correctOptions.size() != submittedOptions.size()) {
            return false;
        }

        // 정답 옵션들을 Map으로 변환 (optionName -> optionValue)
        Map<String, String> correctOptionMap = correctOptions.stream()
                .collect(Collectors.toMap(
                        ProductOption::getOptionName,
                        ProductOption::getOptionValue
                ));

        // 제출한 옵션들을 Map으로 변환
        Map<String, String> submittedOptionMap = submittedOptions.stream()
                .collect(Collectors.toMap(
                        TestSubmitDto.SubmittedOptionDto::getOptionName,
                        TestSubmitDto.SubmittedOptionDto::getOptionValue
                ));

        // 모든 옵션이 정확히 일치하는지 확인
        return correctOptionMap.equals(submittedOptionMap);
    }


    private String generateSubmittedAnswer(List<TestSubmitDto.SubmittedProductDto> submittedProducts) {
        return submittedProducts.stream()
                .map(p -> p.getProductName() + " " + p.getQuantity() + "개")
                .collect(Collectors.joining(" + "));
    }

    private String generateFeedback(boolean isCorrect, int correctCount, int totalItems) {
        if (isCorrect) {
            return "정답입니다! 모든 상품을 정확히 주문했습니다.";
        } else {
            int wrongCount = totalItems - correctCount;
            return String.format("틀렸습니다. %d개 항목 중 %d개가 맞고 %d개가 틀렸습니다.",
                    totalItems, correctCount, wrongCount);
        }
    }

    private void saveSolveHistory(Long userId, Test test, TestSubmitDto submitDto, TestResultDto result) {
        // 1. SolveHistory 생성
        SolveHistory solveHistory = new SolveHistory();
        solveHistory.setUser(userRepository.findById(userId).orElseThrow());
        solveHistory.setTest(test);
        solveHistory.setTrue(result.isCorrect());
        solveHistory.setDuration(submitDto.getDuration());

        // 2. SolveHistory 저장
        SolveHistory savedSolveHistory = solveHistoryRepository.save(solveHistory);

        // 3. 제출한 상품들을 SolveHistoryProduct로 저장
        List<SolveHistoryProduct> solveHistoryProducts = submitDto.getSubmittedProducts().stream()
                .map(dto -> {
                    Product product = productRepository.findByName(dto.getProductName())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

                    return SolveHistoryProduct.builder()
                            .solveHistory(savedSolveHistory)
                            .product(product)
                            .quantity(dto.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        savedSolveHistory.setSolveHistoryProducts(solveHistoryProducts);
        solveHistoryProductRepository.saveAll(solveHistoryProducts);
    }

    private void updateProgressToComplete(Long userId, Long testId, int duration) {
        Optional<TestProgress> progressOpt = testProgressRepository
                .findByUserIdAndTestId(userId, testId);

        if (progressOpt.isPresent()) {
            TestProgress progress = progressOpt.get();
            progress.setCurrentStep("완료");
            progress.setElapsedTime(duration);
            progress.setCompleted(true);
            testProgressRepository.save(progress);
        }
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

        // JSON으로 저장된 상품 정보를 DTO로 변환
        if (progress.getSelectedProducts() != null && !progress.getSelectedProducts().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<TestProgressDto.SelectedProductDto> products = mapper.readValue(
                        progress.getSelectedProducts(),
                        mapper.getTypeFactory().constructCollectionType(
                                List.class, TestProgressDto.SelectedProductDto.class
                        )
                );
                dto.setSelectedProducts(products);
            } catch (Exception e) {
                dto.setSelectedProducts(new ArrayList<>());
            }
        } else {
            dto.setSelectedProducts(new ArrayList<>());
        }

        return dto;
    }

    @Override
    public List<WrongTestDto> getWrongTests(Long userId) {
        List<SolveHistory> solveHistories = solveHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId);

        //각 테스트별로 가장 최근 시도만 추출
        Map<Long, SolveHistory> lastHistories = new HashMap<>();
        for (SolveHistory history : solveHistories) {
            Long testId = history.getTest().getId();
            if (!lastHistories.containsKey(testId)) {
                lastHistories.put(testId, history);
            }
        }

        //최신 시도 중 틀린 것만 필터링
        return lastHistories.values().stream()
                .filter(solveHistory -> !solveHistory.isTrue()) // 틀린 것만
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())) // 최신순
                .map(this::convertToWrongTestDto)
                .collect(Collectors.toList());
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
