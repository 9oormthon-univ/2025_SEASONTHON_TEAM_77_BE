package com.teachtouch.backend.ocr.controller;

import com.teachtouch.backend.ocr.serivce.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1.0/ocr")

public class OCRController {

    private final VisionService visionService;

    public OCRController(VisionService visionService) {
        this.visionService = visionService;
    }

    @PostMapping("/extract-ui")
    public String extractText(@RequestParam("file") MultipartFile file) {
        try {
            return visionService.extractUiComponentsFromFile(file);
        } catch (Exception e) {
            return "Failed to extract text: " + e.getMessage();
        }
    }
}