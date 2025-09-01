package com.teachtouch.backend.example.dto;

import com.teachtouch.backend.example.entity.Example;

public record ExampleResponseDTO(
        Long id,
        Long productId,
        int quantity
) {
    public static ExampleResponseDTO fromEntity(Example example){
        return new ExampleResponseDTO(
                example.getId(),
                example.getProductId(),
                example.getQuantity()
        );
    }
}
