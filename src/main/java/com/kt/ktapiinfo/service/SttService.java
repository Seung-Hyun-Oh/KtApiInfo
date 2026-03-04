package com.kt.ktapiinfo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SttService {

    private final WebClient webClient;

    // application.yml에서 값을 읽어옴
    @Value("${kt.api.key}")
    private String apiKey;

    /**
     * WebClient.Builder는 Spring 부트가 자동으로 빈으로 등록해줍니다.
     * 만약 여기서 에러가 난다면 spring-boot-starter-webflux 의존성을 확인하세요.
     */
    public SttService(WebClient.Builder webClientBuilder, @Value("${kt.api.url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<String> transcribeAudio(MultipartFile audioFile, String sttMode) {
        // 1. KT 규격에 따른 Metadata 구성
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("encoding", "lpcm");
        metadata.put("targetLanguage", "ko");
        metadata.put("sttMode", sttMode);

        // 2. Multi-part 바디 생성 (이미지 규격 반영: metadata + audio)
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        // JSON 데이터 파트
        builder.part("metadata", metadata, MediaType.APPLICATION_JSON);
        // 바이너리 오디오 파일 파트
        builder.part("audio", audioFile.getResource(), MediaType.APPLICATION_OCTET_STREAM);

        return webClient.post()
                .uri("/v1/stt/sync") // KT 엔진 상세 경로
                .header("Authorization", "Bearer " + apiKey) // 발급받은 키 입력
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> System.err.println("KT API 호출 에러: " + e.getMessage()));
    }

    /**
     * STT 요청 (POST /v2/voiceRecognize)
     * @param audioFile
     * @param sttMode
     * @return
     */
    public Mono<String> requestStt(MultipartFile audioFile, String sttMode) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("encoding", "lpcm");
        metadata.put("targetLanguage", "ko");
        metadata.put("sttMode", sttMode);

        builder.part("metadata", metadata, MediaType.APPLICATION_JSON);
        builder.part("audio", audioFile.getResource(), MediaType.APPLICATION_OCTET_STREAM);

        return webClient.post()
                .uri("/v2/voiceRecognize")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class); // 여기서 transactionId가 포함된 JSON 응답
    }

    /**
     * 2. STT 결과 조회 (GET /v2/voiceRecognize/{transactionId})
     * @param transactionId
     * @return
     */
    public Mono<String> getSttResult(String transactionId) {
        return webClient.get()
                .uri("/v2/voiceRecognize/{id}", transactionId)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(String.class); // sttStatus: processing 또는 completed 응답
    }
}
