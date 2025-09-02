package com.teachtouch.backend.product.service;

import com.teachtouch.backend.product.dto.ProductRequestDTO;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Long create(ProductRequestDTO dto) {
        Product product = toEntity(dto);
        return productRepository.save(product).getId();
    }

    @Override
    public List<Long> createAll(List<ProductRequestDTO> dtos) {
        List<Product> products = dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        productRepository.saveAll(products);
        return products.stream().map(Product::getId).toList();
    }

    private Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.name())
                .category(dto.category())
                .imageUrl(dto.imageUrl())
                .price(dto.price())
                .build();
    }
}
