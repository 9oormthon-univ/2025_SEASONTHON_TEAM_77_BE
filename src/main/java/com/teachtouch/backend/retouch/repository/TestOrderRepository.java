package com.teachtouch.backend.retouch.repository;

import com.teachtouch.backend.retouch.entity.TestOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestOrderRepository extends JpaRepository<TestOrder, Long> {
}
