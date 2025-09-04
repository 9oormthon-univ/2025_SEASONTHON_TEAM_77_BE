package com.teachtouch.backend.retouch.repository;

import com.teachtouch.backend.retouch.entity.TestOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestOrderProductRepository extends JpaRepository<TestOrderProduct, Long> {
    List<TestOrderProduct> findByTestOrderId(Long TestOrderId);
}
