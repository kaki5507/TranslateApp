package com.trans.translateapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoPayService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String KAKAO_PAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";

    @Value("${kakao.api.secret.key}")
    private String SECRET_KEY; // 여기에 실제 Secret Key를 입력하세요

    public Map<String, Object> kakaoPayReady() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY); // Authorization 헤더 설정

        // 요청 본문 작성 (예제 데이터)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("cid", "TC0ONETIME"); // 가맹점 코드 (테스트용)
        requestBody.put("partner_order_id", "order_id_1234");
        requestBody.put("partner_user_id", "user_id_1234");
        requestBody.put("item_name", "Test Item");
        requestBody.put("quantity", 1);
        requestBody.put("total_amount", 1000);
        requestBody.put("vat_amount", 0);
        requestBody.put("tax_free_amount", 0);
        requestBody.put("approval_url", "https://localhost:8080/kakaoPaySuccess");
        requestBody.put("cancel_url", "https://localhost:8080/kakaoPayCancel");
        requestBody.put("fail_url", "https://localhost:8080/kakaoPayFail");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    KAKAO_PAY_READY_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to call KakaoPay API: HTTP status " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            // 에러 처리
            throw new RuntimeException("Failed to call KakaoPay API: " + e.getMessage(), e);
        }
    }
}
