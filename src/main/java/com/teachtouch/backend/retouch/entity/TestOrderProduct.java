package com.teachtouch.backend.retouch.entity;

import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.entity.ProductOption;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestOrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_order_id")
    private TestOrder testOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity; // 수량 정보

    @OneToMany(mappedBy = "testOrderProduct", cascade = CascadeType.ALL)
    private List<ProductOption> productOptions = new ArrayList<>();

}
