package com.teachtouch.backend.retouch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrongTestDto {
    private Long testId; //테스트ID
    private Long solveHistoryId; //히스토리ID
    private String testTitle; //테스트 제목
    private LocalDateTime testDate; //풀이한 날짜
}
