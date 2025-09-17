package com.teachtouch.backend.ocr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.teachtouch.backend.ocr.serivce.OcrAiService;
import com.teachtouch.backend.ocr.serivce.VisionService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1.0/ocr")
public class OcrToOpenAiController {

    private final OcrAiService ocrAiService;

    public OcrToOpenAiController(OcrAiService ocrAiService) {
        this.ocrAiService = ocrAiService;
    }

    @PostMapping("/ai")
    public String generateGuideFromOcr(@RequestBody String ocrJson) throws IOException {
        return ocrAiService.generateGuideFromJson(ocrJson);
    }

    @PostMapping("/generate-from-image")
    public String generateGuideFromImage(@RequestParam("file") MultipartFile file) {
        return ocrAiService.generateGuideFromImage(file);
    }
}
