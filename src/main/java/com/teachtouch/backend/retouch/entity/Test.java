package com.teachtouch.backend.retouch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; //제목

    @Column(columnDefinition = "TEXT")
    private String description; //설명

    private int timeLimit; //제한시간

    private String difficulty; //난이도

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "test_order_id")
    private TestOrder testOrder; //테스트 주문내역

    @OneToMany(mappedBy = "test", orphanRemoval = true,  cascade = CascadeType.ALL)
    private List<SolveHistory> solveHistories = new ArrayList<>(); //테스트 문제별 사용자 풀이내역

}
