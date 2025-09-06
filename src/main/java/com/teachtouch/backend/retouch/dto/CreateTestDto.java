package com.teachtouch.backend.retouch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestDto {
    private String title;
    private String description;
    private int timeLimit;
    private String difficulty;
    private String testOrderName;
    private List<CreateTestProductDto> products;
}
