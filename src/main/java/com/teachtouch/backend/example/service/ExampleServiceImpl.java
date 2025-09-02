package com.teachtouch.backend.example.service;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;
import com.teachtouch.backend.example.entity.Example;
import com.teachtouch.backend.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExampleServiceImpl implements ExampleService{
    private final ExampleRepository exampleRepository;
    @Override
    public Example createExample(ExampleRequestDTO dto) {

        Example ex = new Example();
        ex.setProductId(dto.productId());
        ex.setQuantity(dto.quantity());
        return exampleRepository.save(ex);
    }
}
