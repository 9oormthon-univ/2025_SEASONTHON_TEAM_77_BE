package com.teachtouch.backend.product.service;

import com.teachtouch.backend.product.dto.ProductRequestDTO;

import java.util.List;

public interface ProductService {
    Long create(ProductRequestDTO dto);
    List<Long> createAll(List<ProductRequestDTO> dtos);
}
