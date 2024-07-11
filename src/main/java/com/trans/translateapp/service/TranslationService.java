package com.trans.translateapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {

    @Value("${deepl.api.key}")
    private String rapidApiKey;

    private static final String TRANSLATE_API_URL = "https://deep-translate1.p.rapidapi.com/language/translate/v2";

    public String translateText(String text, String targetLang) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        text = text.replace("\n", "").replace("\r", ""); // text 줄바꿈 제거
        // Construct the JSON body correctly
        String jsonBody = String.format("{\"q\":\"%s\",\"source\":\"en\",\"target\":\"%s\"}", text, targetLang);

        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url(TRANSLATE_API_URL)
                .post(body)
                .addHeader("x-rapidapi-key", rapidApiKey)
                .addHeader("x-rapidapi-host", "deep-translate1.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();

        System.out.println(" ★1 : " + jsonBody);
        System.out.println(" ★2 : " + request);
        try (Response response = client.newCall(request).execute()) {
            System.out.println(" ★ response 값 : " + response);
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            System.out.println("Response: " + responseBody);

            // Parsing JSON response
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

            // Check for errors in the response
            if (jsonMap.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) jsonMap.get("error");
                int errorCode = (int) error.get("code");
                String errorMessage = (String) error.get("message");
                throw new RuntimeException("Translation API returned an error: " + errorCode + " - " + errorMessage);
            }

            // Process the normal response
            if (jsonMap.containsKey("data") && jsonMap.get("data") instanceof Map) {
                Map<String, Object> dataResponse = (Map<String, Object>) jsonMap.get("data");
                if (dataResponse.containsKey("translations")) {
                    Map<String, Object> translations = (Map<String, Object>) dataResponse.get("translations");
                    if (translations.containsKey("translatedText")) {
                        return (String) translations.get("translatedText");
                    } else {
                        throw new RuntimeException("Translated text not found in response translations");
                    }
                } else {
                    throw new RuntimeException("Invalid response structure: 'translations' field not found or not a Map");
                }
            } else {
                throw new RuntimeException("Invalid response structure: 'data' field not found or not a Map");
            }
        }
    }
}