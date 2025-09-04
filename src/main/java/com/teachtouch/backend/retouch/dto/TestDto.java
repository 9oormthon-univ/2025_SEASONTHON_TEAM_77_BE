package com.teachtouch.backend.retouch.dto;

import com.teachtouch.backend.retouch.entity.Test;
import com.teachtouch.backend.retouch.entity.TestOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {
    private Long id;
    private String title; //제목
    private String description; //설명
    private int timeLimit; //제한시간
    private String difficulty; //난이도
    private TestOrderDto testOrder;

    public TestDto(Test test) {
        this.id = test.getId();
        this.title = test.getTitle();
        this.description = test.getDescription();
        this.timeLimit = test.getTimeLimit();
        this.difficulty = test.getDifficulty();
        this.testOrder = new TestOrderDto(test.getTestOrder());
    }
}
