package com.teachtouch.backend.guide.dto;

import com.teachtouch.backend.guide.entity.Guide;
import com.teachtouch.backend.guide.entity.Step;

import java.util.List;

public record GuideResponseDTO(
        Long id,
        String title,
        String category,
        String description,
        List<StepResponseDTO> steps
) {
    public static GuideResponseDTO fromEntity(Guide guide, boolean includeSteps) {
        return new GuideResponseDTO(
                guide.getId(),
                guide.getTitle(),
                guide.getCategory(),
                guide.getDescription(),
                includeSteps
                        ? guide.getSteps().stream()
                        .filter(step -> step.getParent() == null)
                        .map(StepResponseDTO::fromEntity)
                        .toList()
                        : null
        );
    }
}
