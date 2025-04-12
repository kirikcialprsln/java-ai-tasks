package com.aitasks.models.translation;

public class TranslationResult {
    private final String originalText;
    private final String translatedText;
    private final String targetLanguage;
    
    public TranslationResult(String originalText, String translatedText, String targetLanguage) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.targetLanguage = targetLanguage;
    }
    
    // Getters
    public String getOriginalText() { return originalText; }
    public String getTranslatedText() { return translatedText; }
    public String getTargetLanguage() { return targetLanguage; }
} 