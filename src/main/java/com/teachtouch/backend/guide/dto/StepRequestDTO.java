package com.teachtouch.backend.guide.dto;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;

import java.util.List;
import java.util.Map;

public record StepRequestDTO(
        Long id,
        String stepCode,
        String title,
        String type,
        String content,
        List<Long> productIds,
        List<ExampleRequestDTO> examples,
        List<StepRequestDTO> subSteps,
        Map<String,Object> metadata
) {}
