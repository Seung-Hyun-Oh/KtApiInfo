package com.kt.ktapiinfo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {
    private final WebClient webClient;

    public ChatService(WebClient.Builder webClientBuilder) {
        // KT API 기본 URL 설정
        this.webClient = webClientBuilder.baseUrl("https://api.kt.co.kr").build();
    }

    public Mono<String> getChatResponse(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("query", message);
        body.put("session_id", "user-123"); // 세션 관리용

        return webClient.post()
            .uri("/v1/ai/chatbot") // KT 실제 엔드포인트로 수정
            .header("Authorization", "Bearer YOUR_KT_API_KEY")
            .header("Content-Type", "application/json")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class);
    }
}
