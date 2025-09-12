package com.teachtouch.backend.ocr.controller;

import com.teachtouch.backend.ocr.serivce.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/ocr")
public class OCRController {

    @Autowired
    private VisionService visionService;

    @GetMapping("/extract-ui")
    public String extractUi(@RequestParam("imageUrl") String imageUrl) {
        try {
            return visionService.extractUiComponentsFromImageUrl(imageUrl);
        } catch (Exception e) {
            return "Failed to extract UI components: " + e.getMessage();
        }
    }

}