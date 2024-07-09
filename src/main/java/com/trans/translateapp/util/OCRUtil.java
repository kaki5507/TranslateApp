package com.trans.translateapp.util;

import net.sourceforge.tess4j.Tesseract; // 광학 문자 인식 수행 라이브러리 Tess4j 사용
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

@Component
public class OCRUtil {
    public static String extractText(String imagePath) {
        Tesseract tesseract = new Tesseract(); // 인스턴스 생성 후 OCR 작업 수행 가능
        try {
            return tesseract.doOCR(new java.io.File(imagePath)); // 해당 이미지 파일 객체 선택해서 분석
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Error occurred while extracting text.";
        }
    }
}