package com.teachtouch.backend.product.entity;

import com.teachtouch.backend.retouch.entity.TestOrderProduct;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String optionName; //사이즈, 온도 등

    private String optionValue; //S, M, L, Hot, Iced 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_order_product_id")
    private TestOrderProduct testOrderProduct;

}
