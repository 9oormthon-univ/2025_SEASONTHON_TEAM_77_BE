package com.teachtouch.backend.retouch.dto;

import com.teachtouch.backend.product.dto.ProductResponseDTO;
import com.teachtouch.backend.retouch.entity.TestOrder;
import com.teachtouch.backend.retouch.entity.TestOrderProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderDto {
    private Long id;
    private String name;
    private List<TestOrderProductDto> products;

    public TestOrderDto(TestOrder testOrder) {
        this.id = testOrder.getId();
        this.name = testOrder.getName();
        this.products = testOrder.getTestOrderProducts().stream()
                .map(TestOrderProductDto::new)
                .collect(Collectors.toList());
    }
}
