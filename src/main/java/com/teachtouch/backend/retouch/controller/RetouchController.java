package com.teachtouch.backend.retouch.controller;


import com.teachtouch.backend.retouch.dto.CreateTestDto;
import com.teachtouch.backend.retouch.dto.GetAllTestDto;
import com.teachtouch.backend.retouch.dto.TestDto;
import com.teachtouch.backend.retouch.service.RetouchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/retouch")
@RequiredArgsConstructor
public class RetouchController {

    private final RetouchService retouchService;

    @GetMapping("/test/all")
    public ResponseEntity<List<GetAllTestDto>> getAllTests() {
        List<GetAllTestDto> tests = retouchService.getTestList();
        return ResponseEntity.ok(tests);
    }


    @PostMapping("/test/add")
    public ResponseEntity<TestDto> addTest(@RequestBody CreateTestDto createTestDto) {
        TestDto created = retouchService.addTest(createTestDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/test/{id}")
    public ResponseEntity<TestDto> getTestById(@PathVariable Long id) {
        TestDto test = retouchService.getTestById(id);
        return ResponseEntity.ok(test);
    }

}
