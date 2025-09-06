package com.teachtouch.backend.product.entity;

import com.teachtouch.backend.guide.entity.Guide;
import com.teachtouch.backend.retouch.entity.SolveHistory;
import com.teachtouch.backend.retouch.entity.TestOrder;
import com.teachtouch.backend.retouch.entity.TestOrderProduct;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imageUrl;
    private String category;
    private int price;

    @OneToMany(mappedBy = "product")
    private List<TestOrderProduct> testOrderProducts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "solve_history_id")
    private SolveHistory solveHistory;

    @OneToMany(mappedBy = "product")
    private List<ProductOption> productOptions = new ArrayList<>();

}
