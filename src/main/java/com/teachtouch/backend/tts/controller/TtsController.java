package com.teachtouch.backend.tts.controller;



import com.teachtouch.backend.tts.dto.TtsRequest;
import com.teachtouch.backend.tts.dto.TtsResponse;
import com.teachtouch.backend.tts.service.TtsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/tts")
public class TtsController {

    private final TtsService ttsService;

    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    @PostMapping
    public TtsResponse synthesize(@RequestBody TtsRequest request) throws Exception {
        String audioBase64 = ttsService.synthesizeText(request.getText());
        return new TtsResponse(audioBase64);
    }
}
