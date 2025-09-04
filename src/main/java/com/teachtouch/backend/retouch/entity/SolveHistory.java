package com.teachtouch.backend.retouch.entity;

import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class SolveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isTrue; //정답여부

    private int duration; //소요시간

    @OneToMany(mappedBy = "solveHistory")
    private List<Product> answer; //사용자 제출 정답

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test; //테스트 문제

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; //사용자

    @CreatedDate
    private LocalDateTime createdDate; //풀이한 날짜

}
