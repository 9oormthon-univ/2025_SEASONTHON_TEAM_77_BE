package com.teachtouch.backend.tts.service;

import java.io.IOException;

public interface TtsService {
    String synthesizeText(String text) throws IOException;
}
