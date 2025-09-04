package com.teachtouch.backend.retouch.repository;

import com.teachtouch.backend.retouch.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test,Long> {
}
