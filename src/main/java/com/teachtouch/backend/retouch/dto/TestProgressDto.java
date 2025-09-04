package com.teachtouch.backend.retouch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestProgressDto {
    private Long testId;
    private String currentStep; // 진행상태
    private List<SelectedProductDto> selectedProducts;
    private int elapsedTime; // 경과 시간 (초)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedProductDto {
        private Long productId;
        private String productName;
        private int quantity;
        private int price;
        private String category;
        private String imageUrl;
    }
}
