package com.teachtouch.backend.retouch.dto;

import com.teachtouch.backend.product.dto.ProductOptionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDto {
    private boolean isCorrect; //정답 여부
    private int duration; //소요 시간
    private String feedback; //피드백 메시지
    private List<ProductComparisonDto> productResults; //상품별 채점 결과
    private TestSummaryDto testSummary; //테스트 요약 정보

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductComparisonDto {
        private String productName;
        private int correctQuantity; //정답 수량
        private int submittedQuantity; //제출한 수량
        private List<ProductOptionDto> productOptions; //제출한 옵션
        private boolean isCorrect; //해당 상품 정답 여부
        private String status; //"정답", "갯수 틀림" 등등
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestSummaryDto {
        private String testTitle;
        private String correctAnswer; //정답 (예: "빅맥 2개 + 콜라 2개")
        private String submittedAnswer; //제출한 답안
    }
}
