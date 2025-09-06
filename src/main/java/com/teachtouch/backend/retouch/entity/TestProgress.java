package com.teachtouch.backend.retouch.entity;

import com.teachtouch.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class TestProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 테스트 진행 중인 사용자

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test; // 진행 중인 테스트

    private String currentStep; // 현재 단계

    @Column(columnDefinition = "TEXT")
    private String selectedProducts; // JSON 형태로 현재 선택된 상품들 저장

    private int elapsedTime; // 경과 시간 (초)

    private boolean isCompleted; // 완료 여부

    @CreatedDate
    private LocalDateTime createdDate; // 시작 시간

    @LastModifiedDate
    private LocalDateTime updatedDate; // 마지막 업데이트 시간
}
