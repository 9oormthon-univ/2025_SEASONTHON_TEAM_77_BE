package com.teachtouch.backend.global.config;

import com.teachtouch.backend.ocr.handler.OcrTtsWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final OcrTtsWebSocketHandler ocrTtsWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ocrTtsWebSocketHandler, "/ws/ocr-tts")
                .setAllowedOrigins("*"); // 모든 도메인 허용
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(2 * 1024 * 1024); // 2MB
        container.setMaxBinaryMessageBufferSize(2 * 1024 * 1024); // 2MB
        container.setMaxSessionIdleTimeout(60000L);
        return container;
    }

}
