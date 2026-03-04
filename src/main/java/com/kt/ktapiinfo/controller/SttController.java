package com.kt.ktapiinfo.controller;

import com.kt.ktapiinfo.service.SttService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Tag(name = "KT STT Async API", description = "KT 비동기 음성인식 엔진 연동 컨트롤러 (요청 및 결과 조회)")
@RestController
@RequestMapping("/api/stt")
@RequiredArgsConstructor // 생성자 주입 자동 생성
@CrossOrigin(origins = "http://localhost:3000")
public class SttController {

    private final SttService sttService;

    /**
     * 1단계: STT 요청 (POST /v2/voiceRecognize)
     * 이미지 규격: multipart/form-data로 metadata와 오디오 파일을 보냄
     */
    @Operation(summary = "STT 변환 요청", description = "음성 파일을 업로드하고 작업 ID(transactionId)를 발급받습니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadAudio(
            @Parameter(description = "변환할 오디오 파일")
            @RequestPart("audio") MultipartFile audio,

            @Parameter(description = "STT 모드 (예: recog)")
            @RequestPart("sttMode") String sttMode) {

        // SttService의 요청 메서드 호출
        return sttService.requestStt(audio, sttMode);
    }

    /**
     * 2단계: STT 결과 조회 (GET /v2/voiceRecognize/{transactionId})
     * 이미지 규격: 발급받은 ID로 상태(processing/completed)를 조회함
     */
    @Operation(summary = "STT 결과 조회", description = "발급받은 transactionId를 사용하여 변환 상태 및 결과를 조회합니다.")
    @GetMapping("/result/{transactionId}")
    public Mono<String> getResult(
            @Parameter(description = "발급받은 작업 ID")
            @PathVariable("transactionId") String transactionId) {

        // SttService의 결과 조회 메서드 호출
        return sttService.getSttResult(transactionId);
    }
}