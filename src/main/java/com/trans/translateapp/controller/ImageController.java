package com.trans.translateapp.controller;

import com.trans.translateapp.service.TranslationService;
import com.trans.translateapp.util.OCRUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // URL 빌더 제공
import com.fasterxml.jackson.databind.ObjectMapper;

// 파일 및 경로 관련 작업 처리
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// 고유 식별자 생성 클래스
import java.util.UUID;

@RestController // 모든 자원 고유 URL로 식별 , JSON ,XML 등 데이터 형식으로 응답을 반환되는데 사용 @Controller + @ResponseBody를 합친 것과 같은 역할
public class ImageController {

    private static final String UPLOAD_DIR = "uploads/"; // 메모리에 하나의 인스턴스만 존재하여 모든 인스턴스 이걸 공유 , 클래스 공용 상수임을 명시(불변의 값)

    @Autowired // 스프링 의존성 주입 필요한 빈 주입 받음
    private TranslationService translationService; // 번역 처리 위한 서비스 클래스

    @Autowired
    private OCRUtil ocrUtil; // OCR 처리를 위한 서비스 클래스

    @Autowired
    private ObjectMapper objectMapper; // JSON 처리에 사용하기 위하여 Jackson 라이브러리 클래스

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) { // 클라이언트가 업로드 한 파일을 받음 input image 받음 RequestParam으로
        try {

            // 파일이 비어있으면 상태코드 400과 오류 메시지를 반환
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Please select an image.\"}"); // json 타입으로 큰 따옴표 표현하려고 escape 문자 표현
            }

            // 파일 이름 생성
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(UPLOAD_DIR); // 업로드 경로 설정
            if (!Files.exists(uploadPath)) { // 디렉토리 경로 있는지 체크
                Files.createDirectories(uploadPath); // 파일 생성 구문
            }
            Path filePath = uploadPath.resolve(fileName); // uploadpath에 fileName을 추가하여 Path객체를 생성
            Files.write(filePath, file.getBytes()); // 파일 저장

            // 저장된 이미지 url 클라이언트에게 반환
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok().body("{\"imageUrl\": \"" + imageUrl + "\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Failed to upload image.\"}");
        }
    }

    @GetMapping("/translate")
    public ResponseEntity<?> translateImage() {
        try {
            Path filePath = Files.list(Paths.get(UPLOAD_DIR)) // 스트림으로 파일 목록 가져옴
                    .filter(Files::isRegularFile) // 디렉토리가 아닌 파일만 필터링
                    .max((p1, p2) -> Long.compare(p1.toFile().lastModified(), p2.toFile().lastModified())) // 가장 최근 수정 된 파일 찾음
                    .orElseThrow(() -> new IOException("No files found.")); // 파일 없으면 예외

            // 번역한 텍스트
            String extractedText = ocrUtil.extractText(filePath.toString());

            // 추출된 텍스트 번역 + 확장성 고려 , 해당 service에서 다르게 번역 가능 ex) 일본어
            String translatedText = translationService.translateText(extractedText);

            // Create JSON response
            TranslationResponse response = new TranslationResponse(translatedText);
            String jsonResponse = objectMapper.writeValueAsString(response); // writeValueAsString으로 객체를 JSON 문자열로 직렬화 시킴

            HttpHeaders headers = new HttpHeaders(); // 응답 타입
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(jsonResponse, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"이미지 번역 실패\"}");
        }
    }

    static class TranslationResponse {
        private String translatedText;

        public TranslationResponse(String translatedText) {
            this.translatedText = translatedText;
        }

        public String getTranslatedText() {
            return translatedText;
        }

        public void setTranslatedText(String translatedText) {
            this.translatedText = translatedText;
        }
    }
}
