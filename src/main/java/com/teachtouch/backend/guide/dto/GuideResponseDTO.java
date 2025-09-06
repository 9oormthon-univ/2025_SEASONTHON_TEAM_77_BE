package com.teachtouch.backend.guide.dto;

import com.teachtouch.backend.guide.entity.Guide;

import java.util.List;

public record GuideResponseDTO(
        Long id,
        String title,
        String category,
        String description,
        Boolean completed,               
        List<StepResponseDTO> steps
) {
    public static GuideResponseDTO fromEntity(Guide guide, boolean includeSteps) {
        return new GuideResponseDTO(
                guide.getId(),
                guide.getTitle(),
                guide.getCategory(),
                guide.getDescription(),
                false, // 기본값
                includeSteps
                        ? guide.getSteps().stream()
                        .filter(step -> step.getParent() == null)
                        .map(StepResponseDTO::fromEntity)
                        .toList()
                        : null
        );
    }
    public static GuideResponseDTO fromEntity(Guide guide, boolean includeSteps, boolean completed) {
        return new GuideResponseDTO(
                guide.getId(),
                guide.getTitle(),
                guide.getCategory(),
                guide.getDescription(),
                completed,
                includeSteps
                        ? guide.getSteps().stream()
                        .filter(step -> step.getParent() == null)
                        .map(StepResponseDTO::fromEntity)
                        .toList()
                        : null
        );
    }
}
