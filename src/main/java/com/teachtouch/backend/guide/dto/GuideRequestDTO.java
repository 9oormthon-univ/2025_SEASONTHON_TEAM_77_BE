package com.teachtouch.backend.guide.dto;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;

import java.util.List;

public record GuideRequestDTO(
        Long id,
        String title,
        String category,
        String description,
        List<Long>productIds,
        List<ExampleRequestDTO>examples,
        List<StepRequestDTO> steps
) {
}