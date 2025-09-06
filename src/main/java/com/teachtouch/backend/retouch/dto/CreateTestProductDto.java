package com.teachtouch.backend.retouch.dto;

import com.teachtouch.backend.product.dto.CreateProductOptionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestProductDto {
    private String name;
    private String category;
    private int price;
    private String imageUrl;
    private int quantity;
    private List<CreateProductOptionDto> productOptions;
}