package com.teachtouch.backend.retouch.service;

import com.teachtouch.backend.retouch.dto.CreateTestDto;
import com.teachtouch.backend.retouch.dto.GetAllTestDto;
import com.teachtouch.backend.retouch.dto.TestDto;

import java.util.List;
import java.util.Optional;

public interface RetouchService {
    List<GetAllTestDto> getTestList();
    TestDto getTestById(Long id);
    TestDto addTest(CreateTestDto createTestDto);
}
