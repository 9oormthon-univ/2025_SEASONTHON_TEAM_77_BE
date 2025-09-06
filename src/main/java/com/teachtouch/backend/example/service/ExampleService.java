package com.teachtouch.backend.example.service;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;
import com.teachtouch.backend.example.entity.Example;
import com.teachtouch.backend.product.dto.ProductRequestDTO;

public interface ExampleService {
    Example createExample(ExampleRequestDTO dto);
}
