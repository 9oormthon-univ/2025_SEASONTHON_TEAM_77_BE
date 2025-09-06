package com.teachtouch.backend.retouch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSubmitDto {
    private Long testId;
    private List<SubmittedProductDto> submittedProducts;
    private int duration; // 소요 시간 (초)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmittedProductDto {
        //private Long productId;
        private String productName;
        private int quantity;
        private List<SubmittedOptionDto> productOptions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmittedOptionDto {
        private String optionName;
        private String optionValue;
    }
}