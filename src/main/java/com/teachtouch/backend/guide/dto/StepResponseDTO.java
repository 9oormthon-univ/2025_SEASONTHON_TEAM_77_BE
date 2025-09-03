package com.teachtouch.backend.guide.dto;

import com.teachtouch.backend.guide.entity.Step;

import java.util.List;
import java.util.Map;

public record StepResponseDTO(
        Long id,
        String stepCode,
        String title,
        String type,
        String content,
        Map<String, Object> metadata,
        List<StepResponseDTO> subSteps
) {
    public static StepResponseDTO fromEntity(Step step) {
        return new StepResponseDTO(
                step.getId(),
                step.getStepCode(),
                step.getTitle(),
                step.getType(),
                step.getContent(),
                step.getMetadata(),
                step.getSubSteps().stream()
                        .map(StepResponseDTO::fromEntity)
                        .toList()
        );
    }
}
