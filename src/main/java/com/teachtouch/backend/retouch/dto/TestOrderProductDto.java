package com.teachtouch.backend.retouch.dto;

import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.retouch.entity.TestOrderProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderProductDto {
    private Long id;
    private String productName;
    private String category;
    private int price;
    private String imageUrl;
    private int quantity;

    public TestOrderProductDto(TestOrderProduct testOrderProduct) {
        Product product = testOrderProduct.getProduct();
        this.id = product.getId();
        this.productName = product.getName();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.quantity = testOrderProduct.getQuantity();
    }

}
