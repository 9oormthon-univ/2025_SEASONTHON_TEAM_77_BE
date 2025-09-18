package com.teachtouch.backend.retouch.service;

import com.teachtouch.backend.product.dto.ProductOptionDto;
import com.teachtouch.backend.product.entity.ProductOption;
import com.teachtouch.backend.retouch.dto.TestResultDto;
import com.teachtouch.backend.retouch.dto.TestSubmitDto;
import com.teachtouch.backend.retouch.entity.Test;
import com.teachtouch.backend.retouch.entity.TestOrderProduct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TestGrader {

    public TestResultDto grade(Test test, TestSubmitDto submitDto) {
        List<TestOrderProduct> correctAnswers = test.getTestOrder().getTestOrderProducts();
        List<TestResultDto.ProductComparisonDto> productResults = new ArrayList<>();
        int correctCount = 0;

        Map<String, TestOrderProduct> correctProductMap = correctAnswers.stream()
                .collect(Collectors.toMap(
                        top -> normalizeProductName(top.getProduct().getName()),
                        top -> top
                ));

        Map<String, TestSubmitDto.SubmittedProductDto> submittedProductMap = submitDto.getSubmittedProducts().stream()
                .collect(Collectors.toMap(
                        dto -> normalizeProductName(dto.getProductName()),
                        dto -> dto
                ));

        for (TestOrderProduct correctItem : correctAnswers) {
            String normalizedProductName = normalizeProductName(correctItem.getProduct().getName());
            String originalProductName = correctItem.getProduct().getName();
            int correctQuantity = correctItem.getQuantity();

            List<ProductOptionDto> productOptions = correctItem.getProductOptions().stream()
                    .map(option -> new ProductOptionDto(option.getOptionName(), option.getOptionValue()))
                    .collect(Collectors.toList());

            TestResultDto.ProductComparisonDto comparison = new TestResultDto.ProductComparisonDto();
            comparison.setProductName(originalProductName);
            comparison.setCorrectQuantity(correctQuantity);
            comparison.setProductOptions(productOptions);

            TestSubmitDto.SubmittedProductDto submittedProduct = submittedProductMap.get(normalizedProductName);

            if (submittedProduct == null) {
                comparison.setSubmittedQuantity(0);
                comparison.setStatus("목록에서 빠짐");
                comparison.setCorrect(false);
                comparison.setDetailedResult(new TestResultDto.DetailedGradingResult(false, false, false));
            } else {
                int submittedQuantity = submittedProduct.getQuantity();
                comparison.setSubmittedQuantity(submittedQuantity);

                TestResultDto.DetailedGradingResult detailedResult = performDetailedGrading(correctItem, submittedProduct);
                comparison.setDetailedResult(detailedResult);

                boolean allCorrect = detailedResult.isMenuSelection() && detailedResult.isSizeSelection() && detailedResult.isQuantitySelection();

                if (allCorrect) {
                    comparison.setStatus("정답");
                    comparison.setCorrect(true);
                    correctCount++;
                } else {
                    if (!detailedResult.isQuantitySelection()) {
                        comparison.setStatus("수량 틀림");
                    } else if (!detailedResult.isSizeSelection()) {
                        comparison.setStatus("사이즈 틀림");
                    } else {
                        comparison.setStatus("메뉴 틀림");
                    }
                    comparison.setCorrect(false);
                }
            }
            productResults.add(comparison);
        }

        for (TestSubmitDto.SubmittedProductDto submitted : submitDto.getSubmittedProducts()) {
            String normalizedName = normalizeProductName(submitted.getProductName());
            if (!correctProductMap.containsKey(normalizedName)) {
                TestResultDto.ProductComparisonDto comparison = new TestResultDto.ProductComparisonDto();
                comparison.setProductName(submitted.getProductName());
                comparison.setCorrectQuantity(0);
                comparison.setSubmittedQuantity(submitted.getQuantity());
                comparison.setStatus("추가 상품");
                comparison.setCorrect(false);

                TestResultDto.DetailedGradingResult detailedResult = performDetailedGradingForExtraProduct(correctAnswers, submitted);
                comparison.setDetailedResult(detailedResult);

                if (submitted.getProductOptions() != null && !submitted.getProductOptions().isEmpty()) {
                    List<ProductOptionDto> submittedOptions = submitted.getProductOptions().stream()
                            .map(option -> new ProductOptionDto(option.getOptionName(), option.getOptionValue()))
                            .collect(Collectors.toList());
                    comparison.setProductOptions(submittedOptions);
                }
                productResults.add(comparison);
            }
        }

        boolean isCorrect = correctCount == correctAnswers.size() && submittedProductMap.size() == correctProductMap.size();
        String feedback = generateFeedback(isCorrect, correctCount, correctAnswers.size());

        TestResultDto.TestSummaryDto testSummary = new TestResultDto.TestSummaryDto();
        testSummary.setTestTitle(test.getTitle());
        testSummary.setCorrectAnswer(test.getTestOrder().getName());
        testSummary.setSubmittedAnswer(generateSubmittedAnswer(submitDto.getSubmittedProducts()));

        TestResultDto result = new TestResultDto();
        result.setCorrect(isCorrect);
        result.setDuration(submitDto.getDuration());
        result.setFeedback(feedback);
        result.setProductResults(productResults);
        result.setTestSummary(testSummary);

        return result;
    }

    private TestResultDto.DetailedGradingResult performDetailedGradingForExtraProduct(
            List<TestOrderProduct> correctAnswers,
            TestSubmitDto.SubmittedProductDto submittedProduct) {
        boolean menuCorrect = false;
        boolean quantityCorrect = false;
        boolean sizeCorrect = false;

        for (TestOrderProduct correctItem : correctAnswers) {
            if (correctItem.getQuantity() == submittedProduct.getQuantity()) {
                quantityCorrect = true;
            }
            if (checkSizeOption(correctItem.getProductOptions(), submittedProduct.getProductOptions())) {
                sizeCorrect = true;
            }
        }
        return new TestResultDto.DetailedGradingResult(menuCorrect, sizeCorrect, quantityCorrect);
    }

    private String normalizeProductName(String productName) {
        return productName != null ? productName.trim().toLowerCase() : "";
    }

    private TestResultDto.DetailedGradingResult performDetailedGrading(
            TestOrderProduct correctItem,
            TestSubmitDto.SubmittedProductDto submittedProduct) {
        boolean menuCorrect = normalizeProductName(correctItem.getProduct().getName())
                .equals(normalizeProductName(submittedProduct.getProductName()));
        boolean quantityCorrect = correctItem.getQuantity() == submittedProduct.getQuantity();
        boolean sizeCorrect = checkSizeOption(correctItem.getProductOptions(), submittedProduct.getProductOptions());
        return new TestResultDto.DetailedGradingResult(menuCorrect, sizeCorrect, quantityCorrect);
    }

    private boolean checkSizeOption(List<ProductOption> correctOptions, List<TestSubmitDto.SubmittedOptionDto> submittedOptions) {
        if (correctOptions == null) correctOptions = new ArrayList<>();
        if (submittedOptions == null) submittedOptions = new ArrayList<>();

        String correctSize = correctOptions.stream()
                .filter(option -> "사이즈".equals(option.getOptionName()))
                .map(ProductOption::getOptionValue)
                .findFirst()
                .orElse(null);

        String submittedSize = submittedOptions.stream()
                .filter(option -> "사이즈".equals(option.getOptionName()))
                .map(TestSubmitDto.SubmittedOptionDto::getOptionValue)
                .findFirst()
                .orElse(null);

        return Objects.equals(correctSize, submittedSize);
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
}
