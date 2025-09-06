package com.teachtouch.backend.example.controller;

import com.teachtouch.backend.example.dto.ExampleRequestDTO;
import com.teachtouch.backend.example.dto.ExampleResponseDTO;
import com.teachtouch.backend.example.entity.Example;
import com.teachtouch.backend.example.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/examples")
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService exampleService;

    @PostMapping
    public ResponseEntity<ExampleResponseDTO> createExample(@RequestBody ExampleRequestDTO dto){
        Example saved = exampleService.createExample(dto);
        return ResponseEntity.ok(ExampleResponseDTO.fromEntity(saved));
    }

}
