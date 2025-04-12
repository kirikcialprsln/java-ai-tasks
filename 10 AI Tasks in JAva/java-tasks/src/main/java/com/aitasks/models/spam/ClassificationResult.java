package com.aitasks.models.spam;

public class ClassificationResult {
    private final String emailContent;
    private final boolean isSpam;
    private final double spamProbability;
    private final String wittyResponse;
    
    public ClassificationResult(String emailContent, boolean isSpam, 
                              double spamProbability, String wittyResponse) {
        this.emailContent = emailContent;
        this.isSpam = isSpam;
        this.spamProbability = spamProbability;
        this.wittyResponse = wittyResponse;
    }
    
    // Getters
    public String getEmailContent() { return emailContent; }
    public boolean isSpam() { return isSpam; }
    public double getSpamProbability() { return spamProbability; }
    public String getWittyResponse() { return wittyResponse; }
    
    @Override
    public String toString() {
        return String.format("""
            📧 Email Classification:
            Content: %s
            Verdict: %s
            Confidence: %.2f%%
            Response: %s
            """,
            emailContent,
            isSpam ? "SPAM" : "HAM",
            spamProbability * 100,
            wittyResponse
        );
    }
} 