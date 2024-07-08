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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class ImageController {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private TranslationService translationService;

    @Autowired
    private OCRUtil ocrUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\": \"Please select an image.\"}");
            }

            // Generate a unique file name to avoid duplicates
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Save the uploaded file to the server
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            // Return the image URL
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
            Path filePath = Files.list(Paths.get(UPLOAD_DIR))
                    .filter(Files::isRegularFile)
                    .max((p1, p2) -> Long.compare(p1.toFile().lastModified(), p2.toFile().lastModified()))
                    .orElseThrow(() -> new IOException("No files found."));

            // Perform OCR on the image
            String extractedText = ocrUtil.extractText(filePath.toString());

            // Translate the extracted text
            String translatedText = translationService.translateText(extractedText);

            // Create JSON response
            TranslationResponse response = new TranslationResponse(translatedText);
            String jsonResponse = objectMapper.writeValueAsString(response);

            HttpHeaders headers = new HttpHeaders();
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
