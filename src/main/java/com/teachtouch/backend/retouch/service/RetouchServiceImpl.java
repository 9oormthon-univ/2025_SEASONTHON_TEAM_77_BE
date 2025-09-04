package com.teachtouch.backend.retouch.service;

import com.teachtouch.backend.product.dto.ProductResponseDTO;
import com.teachtouch.backend.product.entity.Product;
import com.teachtouch.backend.product.repository.ProductRepository;
import com.teachtouch.backend.retouch.dto.*;
import com.teachtouch.backend.retouch.entity.Test;
import com.teachtouch.backend.retouch.entity.TestOrder;
import com.teachtouch.backend.retouch.entity.TestOrderProduct;
import com.teachtouch.backend.retouch.repository.TestOrderProductRepository;
import com.teachtouch.backend.retouch.repository.TestRepository;
import com.teachtouch.backend.retouch.repository.TestOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RetouchServiceImpl implements RetouchService {

    private final TestRepository testRepository;
    private final ProductRepository productRepository;

    @Override
    public List<GetAllTestDto> getTestList() {
        List<GetAllTestDto> tests = testRepository.findAll()
                .stream()
                .map(GetAllTestDto::new)
                .collect(Collectors.toList());
        if(tests.isEmpty()){
            throw new IllegalArgumentException("테스트가 존재하지 않습니다");
        }
        return tests;
    }

    @Override
    public TestDto getTestById(Long id) {
        Test test =  testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + id));
        return new TestDto(test);
    }

    @Override
    public TestDto addTest(CreateTestDto createTestDto) {
        Test test = new Test();
        test.setTitle(createTestDto.getTitle());
        test.setDescription(createTestDto.getDescription());
        test.setTimeLimit(createTestDto.getTimeLimit());
        test.setDifficulty(createTestDto.getDifficulty());

        TestOrder testOrder = new TestOrder();
        testOrder.setName(createTestDto.getTestOrderName());
        testOrder.setTest(test);

        List<TestOrderProduct> testOrderProducts = createTestDto.getProducts().stream()
                .map(productDto -> {
                    Product product = productRepository.findByName(productDto.getName())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productDto.getName()));
                    return TestOrderProduct.builder()
                            .testOrder(testOrder)
                            .product(product)
                            .quantity(productDto.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        testOrder.setTestOrderProducts(testOrderProducts);
        test.setTestOrder(testOrder);

        Test savedTest = testRepository.save(test);

        return new TestDto(savedTest);
    }

}
