package com.teachtouch.backend.ocr.serivce;

import com.google.cloud.vision.v1.Image;
import org.springframework.web.multipart.MultipartFile;

public interface VisionService {
    String extractUiComponentsFromFile(MultipartFile file) throws Exception;
    String extractTextFromImage(Image img) throws Exception;
}
