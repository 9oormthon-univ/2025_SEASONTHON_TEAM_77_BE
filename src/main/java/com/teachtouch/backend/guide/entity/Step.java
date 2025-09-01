package com.teachtouch.backend.guide.entity;

import com.teachtouch.backend.guide.converter.MetadataConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "steps")
@Getter @Setter @NoArgsConstructor
public class Step {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stepCode;     // "1", "1-1", "2-2" 계층형 구조
    private String title;
    private String type;

    @Column(columnDefinition = "TEXT") // 설명이 길어질 것을 대비
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_step_id")
    private Step parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> subSteps = new ArrayList<>();

    @Column(columnDefinition = "json")
    @Convert(converter = MetadataConverter.class)
    private Map<String,Object> metadata;
}

