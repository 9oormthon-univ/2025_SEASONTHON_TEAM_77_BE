package com.teachtouch.backend.ocr.serivce;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OcrAiService {
    String generateGuideFromJson(String ocrJson) throws IOException;
    String generateGuideFromImage(MultipartFile file);
}
