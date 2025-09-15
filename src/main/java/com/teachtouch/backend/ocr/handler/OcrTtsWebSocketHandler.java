package com.teachtouch.backend.ocr.handler;

import com.google.protobuf.ByteString;
import com.teachtouch.backend.ocr.serivce.VisionService;
import com.teachtouch.backend.tts.service.TtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import com.google.cloud.vision.v1.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class OcrTtsWebSocketHandler extends AbstractWebSocketHandler {

    private final VisionService visionService;
    private final TtsService ttsService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final Map<String, ByteArrayOutputStream> sessionBuffers = new ConcurrentHashMap<>();
    private final Map<String, Long> lastMessageTime = new ConcurrentHashMap<>();

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        System.out.println("=== Binary Message 수신됨 ===");
        System.out.println("메시지 크기: " + message.getPayloadLength() + " bytes");

        ByteBuffer byteBuffer = message.getPayload();

        String sessionId = session.getId();

        sessionBuffers.computeIfAbsent(sessionId, k -> new ByteArrayOutputStream());
        ByteArrayOutputStream buffer = sessionBuffers.get(sessionId);

        try {
            // 바이너리 데이터를 버퍼에 추가
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
            buffer.write(data);

            // 마지막 메시지 시간 업데이트
            lastMessageTime.put(sessionId, System.currentTimeMillis());

            System.out.println("누적 데이터 크기: " + buffer.size() + " bytes");

            // ===== 기존의 즉시 처리 로직을 대기 후 처리로 변경 =====
            executorService.submit(() -> {
                try {
                    // 100ms 대기 (추가 메시지가 올지 확인)
                    Thread.sleep(100);

                    // 대기 후에도 새 메시지가 없으면 완성된 이미지로 처리
                    if (System.currentTimeMillis() - lastMessageTime.get(sessionId) >= 100) {
                        processCompleteImage(session, sessionId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

        } catch (IOException e) {
            System.err.println("버퍼 쓰기 오류: " + e.getMessage());
        }
    }

    private void processCompleteImage(WebSocketSession session, String sessionId) {
        ByteArrayOutputStream buffer = sessionBuffers.get(sessionId);
        if (buffer == null || buffer.size() == 0) {
            return;
        }

        try {
            // 모든 조각을 합친 완전한 이미지 데이터
            byte[] completeImageData = buffer.toByteArray();
            System.out.println("=== 완성된 이미지 처리 시작 ===");
            System.out.println("전체 이미지 크기: " + completeImageData.length + " bytes");

            // 기존 로직과 동일 (단, 완성된 이미지로 처리)
            ByteString imgBytes = ByteString.copyFrom(completeImageData);
            Image img = Image.newBuilder().setContent(imgBytes).build();

            System.out.println("OCR 처리 시작...");
            String extractedText = visionService.extractTextFromImage(img);
            System.out.println("extractedText: " + extractedText);

            if(extractedText != null && !extractedText.isEmpty() && !extractedText.startsWith("Error:")) {
                System.out.println("TTS 처리 시작...");
                String audioBase64 = ttsService.synthesizeText(extractedText);
                System.out.println("audioBase64 길이: " + (audioBase64 != null ? audioBase64.length() : 0));

                if(session.isOpen()) {
                    session.sendMessage(new TextMessage(audioBase64));
                    System.out.println("클라이언트에게 응답 전송 완료");
                }
            } else {
                System.out.println("OCR 결과가 없거나 오류로 인해 TTS 건너뜀");
            }
        } catch (Exception e) {
            System.err.println("이미지 처리 오류: " + e.getMessage());
            e.printStackTrace();
            try {
                if(session.isOpen()) {
                    session.sendMessage(new TextMessage("Error: " + e.getMessage()));
                }
            } catch (IOException ioException) {
                System.err.println("메세지 전송 오류: " + ioException.getMessage());
            }
        } finally {
            // 처리 완료 후 메모리 정리
            sessionBuffers.remove(sessionId);
            lastMessageTime.remove(sessionId);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("웹소켓 연결 되었습니다: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("웹소켓 연결 해제되었습니다: " +  session.getId() + " 상태: " + status);

        String sessionId = session.getId();
        sessionBuffers.remove(sessionId);
        lastMessageTime.remove(sessionId);
    }

}
