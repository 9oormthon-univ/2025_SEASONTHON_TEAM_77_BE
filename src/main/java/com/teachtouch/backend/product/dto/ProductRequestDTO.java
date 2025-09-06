package com.teachtouch.backend.product.dto;

public record ProductRequestDTO(
        String name,
        String imageUrl,
        String category,
        int price
) {
}
