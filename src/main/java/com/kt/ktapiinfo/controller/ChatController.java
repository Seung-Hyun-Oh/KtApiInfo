package com.kt.ktapiinfo.controller;

import com.kt.ktapiinfo.service.ChatService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000") // React 포트 허용
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Mono<String> ask(@RequestBody Map<String, String> request) {
        return chatService.getChatResponse(request.get("message"));
    }
}
