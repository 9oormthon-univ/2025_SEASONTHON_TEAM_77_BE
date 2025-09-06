package com.teachtouch.backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDto {
    private String optionName; //사이즈, 온도 등
    private String optionValue; //S, M, L, Hot, Iced 등

}
