package com.teachtouch.backend.tts.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class TtsService {

    public String synthesizeText(String text) throws IOException {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {

            // 입력 텍스트
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // 목소리 설정
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // 오디오 설정 (MP3)
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // API 호출
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // base64 인코딩 문자열 반환 (프론트에서 이후 MP3 변환 필요함)
            ByteString audioContents = response.getAudioContent();
            return Base64.getEncoder().encodeToString(audioContents.toByteArray());
        }
    }
}
