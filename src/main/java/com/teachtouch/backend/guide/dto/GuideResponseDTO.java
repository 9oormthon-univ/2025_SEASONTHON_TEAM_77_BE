package com.teachtouch.backend.guide.dto;

import com.teachtouch.backend.guide.entity.Guide;

public record GuideResponseDTO(
        Long id,
        String title,
        String category,
        String description
) {
    public static GuideResponseDTO fromEntity(Guide guide){
        return new GuideResponseDTO(
                guide.getId(),
                guide.getTitle(),
                guide.getCategory(),
                guide.getDescription()
        );
    }
}
