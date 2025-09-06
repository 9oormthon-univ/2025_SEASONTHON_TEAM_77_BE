package com.teachtouch.backend.retouch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestProgressResponseDto {
    private Long progressId;
    private Long testId;
    private String testTitle;
    private String currentStep; //현재단계
    private List<TestProgressDto.SelectedProductDto> selectedProducts;
    private int elapsedTime; //경과시간
    private boolean isCompleted;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
