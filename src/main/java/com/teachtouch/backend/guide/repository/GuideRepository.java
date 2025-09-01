package com.teachtouch.backend.guide.repository;

import com.teachtouch.backend.guide.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends JpaRepository<Guide,Long> {
}
