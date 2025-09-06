package com.teachtouch.backend.retouch.dto;

import com.teachtouch.backend.retouch.entity.Test;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllTestDto {
    private Long id;
    private String title;
    private String description;
    private int timeLimit;
    private String difficulty;

    public GetAllTestDto(Test test) {
        this.id = test.getId();
        this.title = test.getTitle();
        this.description = test.getDescription();
        this.timeLimit = test.getTimeLimit();
        this.difficulty = test.getDifficulty();
    }
}
