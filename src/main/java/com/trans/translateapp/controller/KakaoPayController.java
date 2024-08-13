package com.trans.translateapp.controller;

import com.trans.translateapp.service.KakaoPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class KakaoPayController {


    @Autowired
    private KakaoPayService kakaoPayService;

    @PostMapping("/kakaoPay")
    public Map<String, Object> kakaoPay() {
        return kakaoPayService.kakaoPayReady();
    }

    @GetMapping("/kakaoPaySuccess")
    public String kakaoPaySuccess(@RequestParam Map<String, String> params) {
        // 결제 성공 후 호출될 URL입니다. 실제로는 이곳에서 결제 결과를 검증하는 로직이 필요합니다.
        // params는 카카오페이에서 전달한 결제 정보입니다.
        return "결제가 성공하였습니다. 결제 정보: " + params.toString();
    }

    @GetMapping("/kakaoPayCancel")
    public String kakaoPayCancel() {
        return "결제가 취소되었습니다.";
    }

    @GetMapping("/kakaoPayFail")
    public String kakaoPayFail() {
        return "결제가 실패하였습니다.";
    }
}
