package com.trans.translateapp.service;


import org.springframework.stereotype.Service;

@Service
public class TranslationService {
    public String translateText(String text) {
        return "Translated text: " + text;
    }
}