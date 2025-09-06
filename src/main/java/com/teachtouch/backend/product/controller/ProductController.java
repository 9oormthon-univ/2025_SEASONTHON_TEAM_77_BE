package com.teachtouch.backend.product.controller;

import com.teachtouch.backend.product.dto.ProductRequestDTO;
import com.teachtouch.backend.product.dto.ProductResponseDTO;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.service.ProductService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping
    public ResponseEntity<Long> createOne(@RequestBody ProductRequestDTO dto) {
        Long id = productService.create(dto);
        return ResponseEntity.ok(id);
    }


    @PostMapping("/batch")
    public ResponseEntity<List<Long>> createMultiple(@RequestBody List<ProductRequestDTO> dtos) {
        List<Long> ids = productService.createAll(dtos);
        return ResponseEntity.ok(ids);
    }
}
