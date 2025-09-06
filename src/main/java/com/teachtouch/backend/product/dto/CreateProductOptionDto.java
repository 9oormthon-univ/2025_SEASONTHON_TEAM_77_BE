package com.teachtouch.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductOptionDto {
    private String optionName;
    private String optionValue;
    private int additionalPrice;
    private boolean isRequired;
    private int sortOrder;
}