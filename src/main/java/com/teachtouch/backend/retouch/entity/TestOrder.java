package com.teachtouch.backend.retouch.entity;

import com.teachtouch.backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class TestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //테스트 문제 내역 ex)햄버거2개+음료수1개

    @OneToMany(mappedBy = "testOrder",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestOrderProduct> testOrderProducts = new ArrayList<>(); //테스트 문제에 해당하는 상품들

    @OneToOne(mappedBy = "testOrder")
    private Test test; //테스트 문제

    public List<Product> getProducts() {
        return testOrderProducts.stream()
                .map(TestOrderProduct::getProduct)
                .collect(Collectors.toList());
    }

}
