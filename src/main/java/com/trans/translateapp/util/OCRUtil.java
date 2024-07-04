package com.trans.translateapp.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCRUtil {
    public static String extractText(String imagePath) {
        Tesseract tesseract = new Tesseract();
        try {
            return tesseract.doOCR(new java.io.File(imagePath));
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Error occurred while extracting text.";
        }
    }
}