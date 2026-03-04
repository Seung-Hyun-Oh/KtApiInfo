package com.kt.ktapiinfo.controller;

import com.kt.ktapiinfo.service.SttService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Tag(name = "KT STT API", description = "KT 음성 인식 엔진 연동 컨트롤러")
@RestController
@RequestMapping("/api/stt")
@CrossOrigin(origins = "http://localhost:3000") // React와의 CORS 허용
public class SttController {

    private final SttService sttService;

    public SttController(SttService sttService) {
        this.sttService = sttService;
    }

    @Operation(summary = "음성 파일 업로드 및 텍스트 변환", description = "오디오 파일을 받아 KT STT 엔진으로 변환을 요청합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadAudio(
            @Parameter(description = "음성 파일 (wav, mp3 등)") @RequestPart("audio") MultipartFile audio,
            @Parameter(description = "STT 모드 (예: one-shot, recog)") @RequestPart("sttMode") String sttMode) {

        // 서비스 계층으로 파일과 모드 전달
        return sttService.transcribeAudio(audio, sttMode);
    }
}
