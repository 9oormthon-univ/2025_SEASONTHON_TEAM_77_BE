package com.teachtouch.backend.product.dto;

import com.teachtouch.backend.product.entity.Product;

public record ProductResponseDTO(
        Long id,
        String name,
        String imageUrl,
        String category,
        int price
) {
    public static ProductResponseDTO fromEntity(Product product){
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getCategory(),
                product.getPrice()
        );
    }
}
