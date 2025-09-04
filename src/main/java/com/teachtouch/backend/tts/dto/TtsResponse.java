package com.teachtouch.backend.tts.dto;

public class TtsResponse {
    private String audioContent;

    public TtsResponse(String audioContent) {
        this.audioContent = audioContent;
    }

    public String getAudioContent() {
        return audioContent;
    }
}
