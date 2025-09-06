package com.teachtouch.backend.guide.repository;

import com.teachtouch.backend.guide.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    List<Guide> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String category);
}